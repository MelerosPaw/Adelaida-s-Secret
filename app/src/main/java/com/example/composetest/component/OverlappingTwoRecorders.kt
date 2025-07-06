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

/**
 * Se detiene la grabadora en 1 seg. porque se asume que la otra habrá empezado a grabar
 * como mucho 1 seg. después de ponerla, pero esto es demasiado asumir.
 */
private const val OVERLAPPING_AUDIO_LAPSE = 3000L
private const val AUDIO_CHUNK_DURATION = 60000L

/**
 * Empieza a grabar el siguiente audio antes de cortar el primero. El lapso de tiempo que transcurre
 * entre que empieza a grabar el segundo y deja de grabar el primero no se conoce con seguridad. Hay
 * que dejar algún tiempo de más, pero ese tiempo de más hará que el final del primer audio y el
 * principio del segundo tengan brevemente el mismo contenido.
 *
 * A veces cuando se reproducen los audios peta.
 *
 */
class OverlappingTwoRecorders(
    override val outputFolder: File,
    override val fileName: String,
    override val clipDurationMs: Long? = null,
    override val extension: String = ".m4u",
): AudioRecorder {

    private var mainRecorder: MediaRecorder? = null
    private var secondaryRecorder: MediaRecorder? = null
    private var fileNo: Int = 0
    private var switcherTimer: Timer? = null
    private var stopperTimer: Timer? = null
    private var isRecording: Boolean = false
    private var isMainRecorder: Boolean = true
    private val logger = Logger("GRABADORA")
    override var fileRecordedListener: AudioRecorder.OnFileRecorded? = null

    override fun startRecording() {
        init()
        recordInNextFile()
        isRecording = true
    }

    override fun stopRecording() {
        if (isRecording) {
            isRecording = false

            logger.log("Deteniendo")
            switcherTimer?.cancel()
            switcherTimer = null
            stop(getProperRecorder(isMainRecorder), true)

            stopperTimer?.let {
                it.cancel()
                stop(getProperRecorder(!isMainRecorder), true)
            }

            stopperTimer = null
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

    private fun init() {
        fileNo = 0
        isMainRecorder = true
    }

    private fun recordInNextFile() {
        recordIn(buildFile(fileNo))
    }

    private fun recordIn(file: File) {
        MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }.also {
            storeRecorder(it, isMainRecorder)
            val nombreGrabadora = "principal".takeIf { isMainRecorder } ?: "secundaria"
            logger.log("Grabando ($nombreGrabadora - $it): \"${file.absolutePath}\"")
            scheduleNextChunk()
        }
    }

    private fun storeRecorder(recorder: MediaRecorder, isMainRecorder: Boolean) {
        if (isMainRecorder) {
            mainRecorder = recorder
        } else {
            secondaryRecorder = recorder
        }
    }

    private fun getProperRecorder(main: Boolean) = mainRecorder.takeIf { main } ?: secondaryRecorder

    private fun scheduleNextChunk() {
        if (switcherTimer == null) {
            val t: TimerTask = timerTask {
                if (isRecording) {
                    scheduleStopRecorder(getProperRecorder(isMainRecorder))
                    fileNo++
                    this@OverlappingTwoRecorders.isMainRecorder = !isMainRecorder
                    recordInNextFile()
                }
            }

            switcherTimer = Timer().apply {
                val duration = AUDIO_CHUNK_DURATION
                schedule(t, duration, duration)
            }
        }
    }

    private fun scheduleStopRecorder(recorder: MediaRecorder?) {
        val t: TimerTask = timerTask {
            logger.log("Tarea $this parando $recorder")
            stop(recorder)
            stopperTimer = null
        }

        stopperTimer = Timer().apply {
            val duration = OVERLAPPING_AUDIO_LAPSE
            schedule(t, duration)
        }
    }

    private fun stop(recorder: MediaRecorder?, recordingIsOver: Boolean = false) {
        logger.log("Parando $recorder ${"por finalización".takeIf { recordingIsOver } ?: "por cambio"}")

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