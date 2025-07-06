package com.example.composetest.component

import android.content.Context
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaRecorder
import android.os.Build
import com.example.composetest.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.LinkedList
import java.util.Queue
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timerTask

private const val SAFETY_OFFSET = 500L
private const val TAG = "AudioRecorder"

/**
 * If the user stops the recording and the remaining time before the current clip is changed for a
 * new one is less than the time the [MediaRecorder] would consider a failure due to calling
 * [MediaRecorder.stop] very quickly after [MediaRecorder.start], ends the clip there and starts a
 * new one. This constant represents the maximum remaining time to avoid this error.
 */
private const val SAFETY_THRESHOLD = 1000L

class CasiopeaAudioRecorder(private val context: Context) {

    /** Callback to receive the resulting audio file when it's been recorded. */
    interface OnFileRecorded {

        /**
         * This method will be called in the main thread when an audio file is ready (after
         * [stopRecording] is done closing an audio file).
         */
        fun onFinishedRecording(file: File)

        /**
         * This method will be called whenever a audio file was either closed prematurely, so it
         * can't be used; or discarded because you called [releaseRecorder] while still recording.
         */
        fun onLastAudioIncomplete()
    }

    private val logger = Logger("GRABADORA")
    private var recorder: MediaRecorder? = null
    private var bitRate: Int? = getBestBitRateForACCEncoding()
    private var outputFolder: File? = null
    private var fileName: String? = null
    private var clipDurationMs: Long? = null
    private var extension: String = "m4a"
    private var fileRecordedListener: OnFileRecorded? = null

    private var fileNo: Int = 0
    private var nextClipTimer: Timer? = null
    private var safetyTimersQueue: Queue<Timer> = LinkedList()

    private var recordingStatus: RecordingStatus = RecordingStatus.NOT_INITIALIZED
    private var fileChangeStatus: FileChangeStatus = FileChangeStatus.IDLE
    private var resumeIn: RecordingStatus? = null
    private var statusAfterResumedAsStarted: RecordingStatus? = null

    private var remainingTime: RemainingTime? = null

    /**
     * You must call this method at least once before [startRecording] to configure the recorder.
     * This recorder cannot work without an [outputFolder] and a [fileName]. If not called, the
     * recording won't start.

     * You don't need to call it again after stopRecording unless you want to change the settings.
     * Calling this method between [startRecording] and [stopRecording] won't have any effect.
     *
     * Improve this method if you need to add configuration values to the recorder.
     *
     * @param outputFolder  Destination folder where the files will be saved to.
     * @param fileName  The name of the file produced. If several files result from the recording,
     *                  a suffix will be added to the this file name.
     * @param extension The extension that will be appended to the file, without the dot.
     * @param clipDurationMs    If `null` or not specified, the recording will last until
     *                          [stopRecording] is called. Else, the recording will stop and will
     *                          be restarted in a new file.
     * @param fileRecordedListener A listener to receive a [File] when the recording is over.
     */
    fun setUp(
        outputFolder: File,
        fileName: String,
        extension: String = "m4a",
        clipDurationMs: Long? = null,
        fileRecordedListener: OnFileRecorded? = null,
    ) {
        if (canBeSetUp()) {
            this.outputFolder = outputFolder
            this.fileName = fileName
            this.extension = extension
            this.clipDurationMs = clipDurationMs
            this.fileRecordedListener = fileRecordedListener
            clipDurationMs?.let { remainingTime = RemainingTime(it) }
            readyRecorder()
        }
    }

    fun getStatus(): RecordingStatus = recordingStatus

    fun isInState(vararg status: RecordingStatus): Boolean = recordingStatus in status

    fun startRecording() {
        if (canStartRecording()) {
            init()
            keepOnRecording()
        }
    }

    fun stopRecording() {
        if (canStopRecording()) {
            recordingStatus = RecordingStatus.STOPPED
            logger.log("Parar")
            val resultingFile = buildFile(fileNo)
            fileNo++

            remainingTime?.reset()
            cancelNextFile()
            doIfNonNull(recorder, resultingFile) { currentRecorder, file ->
                scheduleSafetyStopThreshold(currentRecorder, file)
            }
        }
    }

    fun pauseRecording() {
        if (canPauseRecording()) {
            recordingStatus = RecordingStatus.PAUSED
            val remainingTime = remainingTime?.onPaused()

            if (remainingTime isLessOrEqualTo SAFETY_THRESHOLD) {
                executeFileChange()
                resumeIn = RecordingStatus.STARTED
            } else {
                recorder?.pause()
                cancelNextFile()
            }
        }
    }

    fun resumeRecording() {
        if (canResumeRecording()) {
            if (resumeIn?.isAny(RecordingStatus.PAUSED, RecordingStatus.STARTED) == true) {
                resumeIn = null
                record()

            } else if (recordingStatus == RecordingStatus.STOPPED) {
                record()

            } else {
                recorder?.resume()
                recordingStatus = RecordingStatus.STARTED
                remainingTime?.onStarted()?.let(::scheduleNextClip)
            }
        }
    }

    /**
     * Call this only if you have no more use for this recorder anymore (when closing the activity
     * that uses the recorder or after knowing that you're not using the recorder for a while). It
     * is safe to call this method at any moment, even if the recorder is recording. It will handle
     * resource release on its own. Disclaimer:
     *
     *  * Don't call this if you have to keep recording through several activities or while the
     *  application is minimized. Call it once you're done.
     *  * If you call this method while the recorder is recording, it is understood that you don't
     *  care about the audio, so the recorder will be stopped and released, and the audio file will
     *  be discarded.
     *
     * Calling this method several times has no negative effects.
     *
     * You can actually reuse the recorder if you call [setUp] again.
     */
    fun releaseRecorder() {
        when (recordingStatus) {
            RecordingStatus.NOT_INITIALIZED -> { /* no-op The recorder is still null. */ }
            RecordingStatus.NOT_STARTED, RecordingStatus.STOPPED -> releaseWhileNotRecording()
            RecordingStatus.STARTED, RecordingStatus.PAUSED -> releaseWhileRecording()
        }

        recorder = null
        recordingStatus = RecordingStatus.NOT_INITIALIZED
    }

    fun setOnFileRecordedListener(onFileRecordedListener: OnFileRecorded) {
        this.fileRecordedListener = onFileRecordedListener
    }

    private fun init() {
        if (safetyTimersQueue.isNotEmpty()) {
            readyRecorder()
        }
    }

    /**
     * Calling [MediaRecorder.stop] causes the output file to close abruptly, and usually users are
     * unaware of this, therefore, the last part of their audio is clipped out. To avoid this, the
     * recording will actually be stopped [SAFETY_OFFSET] millis after calling [stopRecording].
     *
     * This method must receive the recorder and the output file even if they're instance variables
     * because, if after calling [stopRecording], you call [startRecording] or [setUp] before it has
     * effectively been stopped, the the current recorder and its set up would have been overridden
     * by the new recording's ones. [buildFile] may not be able to generate the same output file
     * and this recorder would be unreachable and would continue to record until the resource is
     * released by the system.
     *
     * @param recorderToStop The recorder at the moment it was stopped.
     * @param file The file produced by the current recorder at the moment it was stopped.
     */
    private fun scheduleSafetyStopThreshold(recorderToStop: MediaRecorder, file: File) {
        val safetyTimer = Timer()
        val safeStop = timerTask {
            stop(recorderToStop, file)

            if (recorderToStop == recorder) {
                readyRecorder()
            }

            safetyTimersQueue.remove(safetyTimer)
        }

        safetyTimer.schedule(safeStop, SAFETY_OFFSET)
        safetyTimersQueue.add(safetyTimer)
    }

    /**
     * When starting a new recording pretty quickly when the previous one isn't done, it will start
     * a new recorder and let the previous one finish.
     */
    private fun readyRecorder() {
        if (safetyTimersQueue.isNotEmpty()) {
            recorder = null
        }

        if (recorder == null) {
            recorder = internalSetUp()
        } else {
            doIfNonNull(recorder, buildFilePath(fileNo)) { currentRecorder, file ->
                currentRecorder.reSetUp(file)
            }
        }
    }

    private fun internalSetUp(): MediaRecorder? =
        buildFile(fileNo)?.absolutePath?.let {
            MediaRecorder().apply { reSetUp(it) }
        } ?: run {
            recordingStatus = RecordingStatus.NOT_INITIALIZED
            logger.log("Impossible to initialize because setUp hasn't been called to provide the " +
                    "output folder and file.")
            null
        }

    private fun MediaRecorder.reSetUp(outputFilePath: String) {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        bitRate?.let(::setAudioEncodingBitRate)
        setOutputFile(outputFilePath)
        prepare()

        if (isInState(RecordingStatus.NOT_INITIALIZED)) {
            recordingStatus = RecordingStatus.NOT_STARTED
        }
    }

    private fun record() {
        recorder?.let {
            it.start()
            recordingStatus = RecordingStatus.STARTED
            val filePath = buildFilePath(fileNo)
            logger.log("Grabando en \"$filePath\"")
            remainingTime?.onStarted()?.let(::scheduleNextClip)
        }
    }

    private fun keepOnRecording() {
        if (canKeepOnRecording()) {
            record()
        }
    }

    /**
     * The recorder can not be set up while it's recording or paused. It must be set up when it
     * hasn't started to record yet (not called [startRecording]) or after it has finished recording
     * completely (having called [stopRecording]).
     */
    private fun canBeSetUp() = isInState(
        RecordingStatus.NOT_INITIALIZED,
        RecordingStatus.NOT_STARTED,
        RecordingStatus.STOPPED
    )

    /** The recorder can only start recording if it wasn't recording already:
     *
     * * It's being started for the first time ([RecordingStatus.NOT_STARTED]).
     * * It was stopped and you want to use it again ([RecordingStatus.STOPPED]).
     */
    private fun canStartRecording() = isInState(
        RecordingStatus.NOT_STARTED,
        RecordingStatus.STOPPED
    )

    /** The recorder can keep recording if:
     *
     * * It's been started for the first time ([RecordingStatus.NOT_STARTED]).
     * * It's already recording ([RecordingStatus.STARTED]).
     * * It was stopped and you want to use it again ([RecordingStatus.STOPPED]).
     */
    private fun canKeepOnRecording() = isInState(
        RecordingStatus.NOT_STARTED,
        RecordingStatus.STOPPED,
        RecordingStatus.STARTED
    )

    private fun canPauseRecording() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && isInState(RecordingStatus.STARTED)
            && proceedOrStoreStatus(RecordingStatus.PAUSED)

    /**
     * The recording can continue if it was paused previously [RecordingStatus.PAUSED]. However, if
     * it was paused while changing files, you can't call [MediaRecorder.resume] on [recorder]
     * because it will be just prepared. In such case the recording status will be
     * [RecordingStatus.STARTED] and [resumeIn] will be to [RecordingStatus.PAUSED]. This way,
     * this method will know that it has to start the [recorder] instead of resuming it.
     */
    private fun canResumeRecording(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && (isInState(RecordingStatus.PAUSED)
            || (isInState(RecordingStatus.STARTED) && resumeIn == RecordingStatus.PAUSED))
            && proceedOrStoreStatus(RecordingStatus.STARTED)

    private fun canStopRecording() = isInState(RecordingStatus.STARTED, RecordingStatus.PAUSED)
            && proceedOrStoreStatus(RecordingStatus.STOPPED)

    /**
     * If the recorder is changing to the next file because the previous one is full, it will
     * store the PAUSED or RESUMED status in case the user pauses or resumes the recording during
     * that time. After the file change is done, the recorder will resume the recording in the
     * stored state. While resuming the stored state, the user input won't accept more input.
     */
    private fun proceedOrStoreStatus(status: RecordingStatus): Boolean {
        when (fileChangeStatus) {
            FileChangeStatus.CHANGING -> resumeIn = status
            FileChangeStatus.RESUMING -> statusAfterResumedAsStarted = status
            FileChangeStatus.IDLE -> { /* Not changing status, nothing to store */ }
        }

        return fileChangeStatus == FileChangeStatus.IDLE
    }

    private fun scheduleNextClip(clipDurationMs: Long) {
        if (nextClipTimer == null) {
            val t: TimerTask = timerTask {
                executeFileChange()
            }

            nextClipTimer = Timer().apply {
                schedule(t, clipDurationMs, clipDurationMs)
            }
        }
    }

    private fun executeFileChange() {
        remainingTime?.reset()
        cancelNextFile()

        if (canStopRecording()) {
            changeFile()
            resumeAfterChangingFile()
        }
    }

    private fun changeFile() {
        fileChangeStatus = FileChangeStatus.CHANGING
        doIfNonNull(recorder, buildFile(fileNo)) { currentRecorder, file ->
            stop(currentRecorder, file)
        }

        fileNo++
        readyRecorder()
    }

    /**
     * While the file is being changed, the user may press the pause or stop buttons, which
     * in turn will trigger calls to [pauseRecording] or [stopRecording]. These state changes
     * will be collected in [resumeIn] while the recorder is changing files so they can be applied
     * after file change is done.
     */
    private fun resumeAfterChangingFile() {
        fileChangeStatus = FileChangeStatus.RESUMING

        when (resumeIn) {
            null, RecordingStatus.STARTED -> resumeAsStarted()
            RecordingStatus.PAUSED -> resumeAsPaused()
            RecordingStatus.STOPPED -> resumeAsStopped()
            RecordingStatus.NOT_INITIALIZED, RecordingStatus.NOT_STARTED -> { /* Mustn't resume. */ }
        }

        fileChangeStatus = FileChangeStatus.IDLE
        verifyStatusAfterResumed()
    }

    /**
     * Re-checks the state of the recorder after [resumeAsStarted] to keep it consistent.
     *
     * [resumeAsStopped] and [resumeAsPaused] present no issues, since after stopping the recording
     * to change files, the [recorder] will simply not be started and that's all.
     *
     * However, with [resumeAsStarted], the [recorder] has to be reset and restarted, which takes a
     * very reduced amount of time to be done. If the user presses start or stop during this precise
     * point in time, [keepOnRecording] will have called [scheduleNextClip] by the end, so we must
     * re-check the state to apply those changes then. This state is stored in
     * [statusAfterResumedAsStarted].
     */
    private fun verifyStatusAfterResumed() {
        if (statusAfterResumedAsStarted != null) {
            if (statusAfterResumedAsStarted == RecordingStatus.STOPPED) {
                stopRecording()

            } else if (statusAfterResumedAsStarted == RecordingStatus.PAUSED) {
                pauseRecording()
            }

            statusAfterResumedAsStarted = null
        }
    }

    private fun resumeAsStarted() {
        keepOnRecording()
        resumeIn = null
    }

    private fun resumeAsPaused() {
        recordingStatus = RecordingStatus.PAUSED
        cancelNextFile()
    }

    private fun resumeAsStopped() {
        recordingStatus = RecordingStatus.STOPPED
        cancelNextFile()
        resumeIn = null
    }

    /**
     * @param resultingFile The resulting audio file will be null only if you're releasing the
     * recorder on demand while it's recording, because it's assumed that you don't want to keep
     * the audio then.
     */
    private fun stop(recorderToStop: MediaRecorder, resultingFile: File?) {
        with(recorderToStop) {
            try {
                stop()
            } catch (e: RuntimeException) {
                (resultingFile ?: buildFile(fileNo))?.let(::deleteAudio)
            }

            if (resultingFile != null) {
                reset()
                doIfNonNull(fileRecordedListener, resultingFile) { listener, file ->
                    CoroutineScope(Dispatchers.Main).launch {
                        listener.onFinishedRecording(file)
                    }
                }
            } else {
                release()
            }
        }
    }

    /**
     * If you dispose of the recorder while it's recording, it's assumed that you don't want to keep
     * the audio. Stops and releases the recorder, deletes the current audio file and prevents the
     * [fileRecordedListener] from being called.
     */
    private fun releaseWhileRecording() {
        recorder?.let {
            stop(it, null)
            remainingTime?.reset()
            cancelNextFile()
            buildFile(fileNo)?.let(::deleteAudio)
        }
    }

    /**
     * The recorder may have been already set up or not, but it's not recording, so simply release
     * it.
     */
    private fun releaseWhileNotRecording() {
        recorder?.release()
    }

    private fun buildFile(fileNumber: Int): File? = buildFilePath(fileNumber)?.let(::File)

    private fun buildFilePath(fileNumber: Int): String? {
        val folderPath = outputFolder?.path
        val name = fileName

        return if (folderPath != null && name != null) {
            "$folderPath/$name-file_$fileNumber.$extension"
        } else {
            null
        }
    }

    private fun deleteAudio(file: File) {
        file.takeIf { it.exists() }?.let {
            logger.log("Deleting unstarted audio file: ${it.absolutePath}")
            it.delete()
            CoroutineScope(Dispatchers.Main).launch {
                fileRecordedListener?.onLastAudioIncomplete()
            }
        }
    }

    private fun cancelNextFile() {
        nextClipTimer?.cancel()
        nextClipTimer = null
    }

    private fun getRecordings(): Array<File> {
        val files = mutableListOf<File>()
        var fileNumber = 0
        var file = buildFile(fileNumber)

        while (file?.exists() == true) {
            files.add(file)
            fileNumber++
            file = buildFile(fileNumber)
        }

        return files.toTypedArray()
    }

    fun deleteAllRecordings() {
        getRecordings().forEach {
            it.takeIf { it.exists() }?.run {
                logger.log("Borrando \"$absolutePath\"")
                delete()
            }
        }

        fileNo = 0
        buildFilePath(fileNo)?.let {
            recorder?.run {
                reset()
                reSetUp(it)
            }
        }
    }

    private fun getBestBitRateForACCEncoding(): Int? =
        MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos
            .mapNotNull { info ->
                info?.takeIf { it.isEncoder }
                    ?.let { getCapabilitiesForACC(it)?.audioCapabilities?.bitrateRange?.upper }
            }.maxOrNull().also {
                logger.log("Best bit rate: $it")
            }

    private fun getCapabilitiesForACC(it: MediaCodecInfo) = try {
        it.getCapabilitiesForType("audio/mp4a-latm")
    } catch (e: Throwable) {
        null
    }

    private fun <T, P1, P2> doIfNonNull(p1: P1?, p2: P2?, onNoneNull: (P1, P2) -> T): T? =
        if (p1 != null && p2 != null) {
            onNoneNull(p1, p2)
        } else {
            null
        }

    private infix fun Long?.isLessOrEqualTo(other: Long) = this != null && this <= other

    class RemainingTime(private val totalTime: Long) {

        private var startedAt: Long? = null
        private var timeRemaining: Long = totalTime
        private var logger = Logger("GRABADORA")

        fun onStarted(): Long {
            startedAt = System.currentTimeMillis()
            logRemainingTime("Started")
            return timeRemaining
        }

        fun onPaused(): Long {
            startedAt?.let {
                timeRemaining -= (System.currentTimeMillis() - it)
                logRemainingTime("Paused")
            }

            return timeRemaining
        }

        fun reset() {
            logRemainingTime("Reset ${startedAt?.let { timeRemaining - (System.currentTimeMillis() -it) }}")
            startedAt = null
            timeRemaining = totalTime
        }

        private fun logRemainingTime(etapa: String) {
            logger.log("$etapa $timeRemaining")
        }
    }

    enum class RecordingStatus(private val order: Int) {

        NOT_INITIALIZED(0), NOT_STARTED(1), STARTED(2), PAUSED(3), STOPPED(4);

        fun isAtLeast(state: RecordingStatus): Boolean = order >= state.order

        fun isAny(vararg status: RecordingStatus): Boolean = this in status
    }

    enum class FileChangeStatus {
        CHANGING, RESUMING, IDLE
    }
}