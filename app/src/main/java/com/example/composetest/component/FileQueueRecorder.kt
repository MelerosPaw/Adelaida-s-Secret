package com.example.composetest.component

import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.composetest.AudioRecorder
import com.example.composetest.Logger
import java.io.File
import java.io.IOException


private const val AUDIO_CHUNK_DURATION_IN_SECS: Int = 4
private const val BIT_RATE: Int = 256000 // 256 kbps

/**
 * No permite poner el siguiente archivo si no es indicando un tamaño máximo de archivo en bytes.
 * No se puede saber el tamaño en bytes que ocupará el archivo en 3 minutos porque el encoder AAC
 * usa una tasa de bits variable y además cambiará durante la grabación además, según indica la
 * documentación de [MediaRecorder.setAudioEncodingBitRate] para ajustarse a las capacidades del
 * dispositivo.
 */
class FileQueueRecorder(
    override val outputFolder: File,
    override val fileName: String,
    override val clipDurationMs: Long? = null,
    override val extension: String = ".m4u",
): AudioRecorder {

    private var recorder: MediaRecorder? = null
    private var fileNo: Int = 0
    private var isRecording: Boolean = false
    private val logger = Logger("GRABADORA")
    override var fileRecordedListener: AudioRecorder.OnFileRecorded? = null

    override fun startRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initialize()
            start(buildFile(fileNo))
        }
    }

    override fun stopRecording() {
        if (isRecording) {
            logger.log("Deteniendo")

            isRecording = false

            try {
                recorder?.stop()
                recorder?.reset()
                recorder?.release()
            } catch (e: RuntimeException) {
                recorder?.reset()
                recorder?.release()
            }
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

    private fun initialize() {
        fileNo = 0
    }

    /**
     * Dividido entre 8 porque BIT_RATE está expresada en bits y setMaxFileSize() la espera en
     * bytes (1 byte == 8 bits).
     */
    private fun calculateMaxFileSize(bitRate: Int, durationMs: Long): Long =
        bitRate * ( durationMs / 1000) / 8L

    @RequiresApi(Build.VERSION_CODES.O)
    private fun start(file: File) {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(BIT_RATE)
            clipDurationMs?.let { setMaxFileSize(calculateMaxFileSize(BIT_RATE, it)) }
            setOutputFile(file)
            setOnInfoListener { mr, what, extra ->
                when (what) {
                    MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_APPROACHING -> prepareNextFile()
                    MediaRecorder.MEDIA_RECORDER_INFO_NEXT_OUTPUT_FILE_STARTED -> onNextFileStarted()
                }
            }

            prepare()
            start()

            isRecording = true
            logger.log("Grabando \"${file.absolutePath}\"")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun MediaRecorder.prepareNextFile() {
        try {
            val nextFile = produceNextFile()
            setNextOutputFile(nextFile)
            logger.log("Preparado archivo \"${nextFile.absolutePath}\"")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun onNextFileStarted() {
        logger.log("Inicia el siguiente.")
        fileRecordedListener?.let { buildFile(fileNo - 1) }
    }

    private fun produceNextFile(): File {
        fileNo++
        return buildFile(fileNo)
    }

    private fun stop() {

    }
}