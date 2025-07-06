package com.example.composetest.component

import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.composetest.AudioRecorder
import com.example.composetest.Logger
import java.io.File
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timerTask

private const val SAFETY_OFFSET = 500L

class FileAfterFileRecorder(
    override val outputFolder: File,
    override val fileName: String,
    override val clipDurationMs: Long? = null,
    override val extension: String = ".m4u",
): AudioRecorder {

    private var recorder: MediaRecorder? = null
    private var fileNo: Int = 0
    private var nextClipTimer: Timer? = null

    private var isRecording: Boolean = false
    private var stopping: Boolean = false
    private var bitRate: Int? = getBestBitRateForACCEncoding()
    private val logger = Logger("GRABADORA")

    override var fileRecordedListener: AudioRecorder.OnFileRecorded? = null

    override fun startRecording() {
        init()
        recordInNextFile()
    }

    override fun stopRecording() {
        if (isRecording && !stopping) {
            isRecording = false
            stopping = true
            logger.log("Deteniendo")

            nextClipTimer?.cancel()
            nextClipTimer = null

            scheduleSafetyStopThreshold()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun pauseRecording() {
        if (isRecording) {
            recorder?.pause()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun resumeRecording() {
        recorder?.resume()
    }

    override fun deleteAllRecordings() {
        super.deleteAllRecordings()
        fileNo = 0
    }

    private fun scheduleSafetyStopThreshold() {
        Timer().schedule(timerTask {
            stop()
            stopping = false
        }, SAFETY_OFFSET)
    }

    override fun buildFile(fileNumber: Int): File =
        File(outputFolder.path + "/$fileName-file_$fileNumber$extension")

    private fun init() {
        fileNo = 0
    }

    private fun recordInNextFile() {
        val file = buildFile(fileNo)

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            bitRate?.let(::setAudioEncodingBitRate)
            setOutputFile(file.absolutePath)
            prepare()
            start()

            logger.log("Grabando \"${file.absolutePath}\"")
            isRecording = true
            clipDurationMs ?.let(::scheduleNextClip)
        }
    }

    private fun scheduleNextClip(clipDurationMs: Long) {
        if (nextClipTimer == null) {
            val t: TimerTask = timerTask {
                if (isRecording) {
                    stop()
                    fileNo++
                    recordInNextFile()
                }
            }

            nextClipTimer = Timer().apply {
                schedule(t, clipDurationMs, clipDurationMs)
            }
        }
    }

    private fun pause() {
        if (isRecording) {
            recorder?.pause()
        }
    }

    private fun stop() {
        try {
            recorder?.stop()
            recorder?.reset()
            recorder?.release()
            fileRecordedListener?.onFinishedRecording(buildFile(fileNo))
        } catch (e: RuntimeException) {
            recorder?.reset()
            recorder?.release()
            deleteCurrentAudio()
        }
    }

    private fun deleteCurrentAudio() {
        buildFile(fileNo).takeIf { it.exists() }?.let {
            logger.log("Borrando último audio: ${it.absolutePath}")
            it.delete()
        }
    }
}