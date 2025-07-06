package com.example.composetest.component

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.composetest.AudioRecorder
import com.example.composetest.Logger
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.thread
import kotlin.concurrent.timerTask


/**
 * Muy complejo. Requiere programación a muy bajo nivel para leer directamente lo que entra por
 * el micrófono, escribirlo en archivos y luego codificar esos archivos para que se consideren
 * archivos de audio.
 */
class AudioRecordRecorder(
    override val outputFolder: File,
    override val fileName: String,
    override val clipDurationMs: Long? = null,
    override val extension: String = ".m4u"
): AudioRecorder {

    private val SAMPLE_RATE: Int = 22050 // Frecuencia de muestreo
    private val CHANNEL_CONFIG: Int = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT: Int = AudioFormat.ENCODING_PCM_8BIT
    private var audioRecord: AudioRecord? = null
    private var bufferSize: Int? = null
    private var swappingFiles: Boolean = false
    private var fileNo: Int = 0
    private var timer: Timer? = null
    private var isRecording: Boolean = false
    private val logger = Logger("GRABADORA")
    override var fileRecordedListener: AudioRecorder.OnFileRecorded? = null

    override fun startRecording() {
        init()
        recordInNextFile()
    }

    override fun stopRecording() {
        if (isRecording) {
            isRecording = false
            logger.log("Deteniendo")
            timer?.cancel()
            timer = null
            audioRecord?.stop()
            audioRecord?.release()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun pauseRecording() {
        // Who knows
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun resumeRecording() {
        // Who knows
    }

    override fun deleteAllRecordings() {
        super.deleteAllRecordings()
        fileNo = 0
    }

    @SuppressLint("MissingPermission")
    private fun init() {
        fileNo = 0
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        audioRecord = bufferSize?.let {
            val record = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                it
            )
            record.startRecording()
            isRecording = true
            record
        }
    }

    @SuppressLint("MissingPermission")
    private fun recordInNextFile() {
        bufferSize?.let {
            val file = buildFile(fileNo)
            dumpOn(file, it) { swappingFiles }
            logger.log("Grabando \"${file.absolutePath}\"")
            clipDurationMs?.let(::scheduleNextChunk)
        }
    }

    private fun scheduleNextChunk(clipDurationMs: Long) {
        if (timer == null) {
            val t: TimerTask = timerTask {
                if (isRecording) {
                    swappingFiles = true
                    fileNo++
                    recordInNextFile()
                }
            }

            timer = Timer().apply {
                schedule(t, clipDurationMs, clipDurationMs)
            }
        }
    }

    private fun dumpOn(file: File, bufferSize: Int, onReadPassFinished: () -> Boolean) {
        thread {
            logger.log("Hilo nuevo")
            val buffer = ByteArray(bufferSize)
            try {
                FileOutputStream(file).use { os ->
                    var stopWriting = false

                    while (isRecording && !stopWriting) {
                        audioRecord?.let {
                            val read = it.read(buffer, 0, buffer.size)
                            logger.log("Escribiendo $buffer")

                            if (read > 0) {
                                os.write(buffer, 0, read)
                            }

                            stopWriting = onReadPassFinished()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            logger.log("Fin del hilo")
            swappingFiles = false
        }
    }
}