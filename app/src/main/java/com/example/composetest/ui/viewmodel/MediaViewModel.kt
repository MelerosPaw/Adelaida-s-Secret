package com.example.composetest.ui.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.composetest.Logger
import com.example.composetest.component.CasiopeaAudioRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context
): ViewModel() {

    private val _grabacionHabilitada: MutableState<PermissionState> = mutableStateOf(PermissionState.SOLICITAR)
    val grabacionHabilitada: State<PermissionState> = _grabacionHabilitada
    private val _isPaused: MutableState<Boolean> = mutableStateOf(false)
    val isPaused: State<Boolean> = _isPaused

    private var audioRecorder: CasiopeaAudioRecorder? = null
    private var player: MediaPlayer? = null
    private val logger: Logger = Logger("GRABADORA")
    private val recordings: MutableList<File> = LinkedList()

    fun setUp() {
//        audioRecorder =
//            FileAfterFileRecorder(context.cacheDir, "scribe_file_after_file", 3000L).also {
//                it.setOnFileRecordedListener(recordings::add)
//            }
//        audioRecorder = AudioRecordRecorder(context.cacheDir, "Grabadora_AudioRecord")
//        audioRecorder = OverlappingTwoRecorders(context, "Grabadora_Solapada") // El bueno
//        audioRecorder = FileQueueRecorder(context, "Grabadora_Cola")
        audioRecorder = CasiopeaAudioRecorder(context).also {
            it.setUp(context.cacheDir, "casiopea", "mp3", 3000L,
                object : CasiopeaAudioRecorder.OnFileRecorded {

                    override fun onFinishedRecording(file: File) {
                        recordings.add(file)
                    }

                    override fun onLastAudioIncomplete() {
                        Toast.makeText(context,"El último audio ha sido cancelado",
                            Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    fun startRecording() {
        logger.log("Empezar")
        audioRecorder?.startRecording()
    }

    fun pauseRecording() {
        logger.log("Pausa")
        audioRecorder?.pauseRecording()
        _isPaused.value = true
    }

    fun resumeRecording() {
        logger.log("Reanudar")
        audioRecorder?.resumeRecording()
        _isPaused.value = false
    }

    fun stopRecording() {
        logger.log("Deteniendo")
        audioRecorder?.stopRecording()
    }

    fun startPlayback() {
        recordings.takeIf { it.isNotEmpty() }
            ?.let { playback(0, it.toTypedArray()) }
    }

    fun stopPlayback(user: Boolean = false) {
        if (player?.isPlaying == true) {
            if (user) {
                logger.log("Parando reproducción")
            }

            player?.stop()
            player?.reset()
            player?.release()
            player = null
        }
    }

    private fun playback(playerTrack: Int, files: Array<File>) {
        val file = files[playerTrack]

        if (file.exists()) {
            player = MediaPlayer.create(context, Uri.fromFile(file)).apply {
                logger.log("Reproduciendo pista $playerTrack: \"${file.path}\"")
                start()
                setOnCompletionListener {
                    playNext(playerTrack, files)
                }
            }
        } else if (playerTrack < files.size - 1) {
            playNext(playerTrack, files)
        } else {
            logger.log("Fin de la cola de reproducción")
        }
    }

    private fun playNext(playerTrack: Int, files: Array<File>) {
        stopPlayback()
        playerTrack.inc().takeIf { files.getOrNull(it) != null }?.let {
            logger.log("Poniendo la siguiente")
            playback(it, files)
        } ?: logger.log("Fin de la cola de reproducción")
    }

    fun deleteAllRecordings() {
        recordings.clear()
        audioRecorder?.deleteAllRecordings()
    }

    fun mostrarControlesGrabacion(estado: PermissionState) {
        _grabacionHabilitada.value = estado
    }

    fun iniciarPermiso(tiene: Boolean, mostrarExplicacion: Boolean) {
        _grabacionHabilitada.value = when {
            tiene -> PermissionState.TENGO
            mostrarExplicacion -> PermissionState.EXPLICACION
            else -> PermissionState.SOLICITAR
        }
    }

    fun onRelease() {
        audioRecorder?.releaseRecorder()
    }

    enum class PermissionState {
        TENGO, SOLICITAR, EXPLICACION, NO_TENGO
    }
}