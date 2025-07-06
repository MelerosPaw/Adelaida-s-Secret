package com.example.composetest

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import java.io.File

/**
 * AudioRecorder
 *
 * @property outputFolder Destination folder where the files will be saved to.
 * @property fileName The name of the file produced. If several files result from the recording,
 * a suffix will be added to the this file name.
 * @property extension The extension that will be appended to the file.
 * @property clipDurationMs If null or not specified, the recording will last until stopRecording()
 * is called. Else, the recording will stop and will be restarted in a new file.
 * @property fileRecordedListener A listener to receive a [File] when the recording is over.
 */
interface AudioRecorder {

    val outputFolder: File
    val fileName: String
    val extension: String
    val clipDurationMs: Long?
    var fileRecordedListener: OnFileRecorded?

    fun interface OnFileRecorded {
        fun onFinishedRecording(file: File)
    }

    fun startRecording()
    fun stopRecording()
    fun pauseRecording()
    fun resumeRecording()

    fun setOnFileRecordedListener(onFileRecordedListener: OnFileRecorded) {
        this.fileRecordedListener = onFileRecordedListener
    }

    fun buildFile(fileNumber: Int): File =
        File(outputFolder.path + "/$fileName-file_$fileNumber$extension")

    fun getRecordings(): Array<File> {
        val files = mutableListOf<File>()
        var fileNumber = 0
        var file = buildFile(fileNumber)

        while (file.exists()) {
            files.add(file)
            fileNumber++
            file = buildFile(fileNumber)
        }

        return files.toTypedArray()
    }

    fun deleteAllRecordings() {
        getRecordings().forEach {
            val logger = Logger("GRABADORA")

            it.takeIf { it.exists() }?.run {
                logger.log("Borrando \"$absolutePath\"")
                delete()
            }
        }
    }

    fun getBestBitRateForACCEncoding(): Int? =
        MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos
            .mapNotNull { info ->
                info?.takeIf { it.isEncoder }
                    ?.let { getCapabilitiesForACC(it)?.audioCapabilities?.bitrateRange?.upper }
            }.maxOrNull().also {
                Logger("GRABADORA").log("Best bit rate $it")
            }

    fun getCapabilitiesForACC(it: MediaCodecInfo) = try {
        it.getCapabilitiesForType("audio/mp4a-latm")
    } catch (e: Throwable) {
        null
    }
}