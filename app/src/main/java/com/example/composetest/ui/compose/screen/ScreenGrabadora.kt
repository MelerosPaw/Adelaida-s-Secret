package com.example.composetest.ui.compose.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composetest.ui.viewmodel.MediaViewModel

const val micPermission: String = Manifest.permission.RECORD_AUDIO

@Preview
@Composable
fun ScreenGrabadora() {
    Screen {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Reproductor(Modifier.padding(innerPadding).padding(16.dp))
        }
    }
}

@Composable
private fun Reproductor(modifier: Modifier) {
    val viewModel = viewModel<MediaViewModel>()
    viewModel.setUp()
    val permission = LocalContext.current.checkSelfPermission(micPermission)
    val hasPermission = permission == PackageManager.PERMISSION_GRANTED
//    val explicacion = shouldShowRequestPermissionRationale(activity, micPermission)
    viewModel.iniciarPermiso(hasPermission, false)

    val estadoControlesGrabacion by remember { viewModel.grabacionHabilitada }

    when (estadoControlesGrabacion) {
        MediaViewModel.PermissionState.TENGO -> ControlesReproductor(viewModel, modifier)
//    MediaViewModel.PermissionState.EXPLICACION -> ExplicacionPermiso(viewModel)
        MediaViewModel.PermissionState.SOLICITAR -> solicitarPermiso(viewModel)
        else -> { /* No pintar UI */ }
    }
}

@Composable
private fun ExplicacionPermiso(viewModel: MediaViewModel) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Necesitamos el permiso") },
        text = { Text("Si quieres grabar, debes conceder el permiso, ¿OK?") },
        confirmButton = {
            Button(
                onClick = { viewModel.mostrarControlesGrabacion(MediaViewModel.PermissionState.SOLICITAR) }
            ) {
                Text("Pues OK")
            }
        },
        dismissButton = {
            Button(
                onClick = { viewModel.mostrarControlesGrabacion(MediaViewModel.PermissionState.NO_TENGO) }
            ) {
                Text("¡Jamás!")
            }
        },
    )
}

@Composable
private fun ControlesReproductor(viewModel: MediaViewModel, modifier: Modifier) {
    Column(Modifier.fillMaxWidth().then(modifier)) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button({ viewModel.startRecording() }) {
                Text("Grabar")
            }

            val estaPausado by remember { viewModel.isPaused }

            Button({ if (estaPausado) { viewModel.resumeRecording() } else { viewModel.pauseRecording() } }) {
                Text("Reanudar".takeIf { estaPausado } ?: "Pausar")
            }

            Button({ viewModel.stopRecording() }) {
                Text("Parar")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button({ viewModel.startPlayback() }) {
                Text("Reproducir todo")
            }

            Button({ viewModel.stopPlayback(true) }) {
                Text("Stop")
            }
        }

        Button({ viewModel.deleteAllRecordings() }, modifier = Modifier.fillMaxWidth()) {
            Text("Borrar audios")
        }
    }
}

@Composable
private fun solicitarPermiso(viewModel: MediaViewModel) {
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { viewModel.mostrarControlesGrabacion(MediaViewModel.PermissionState.NO_TENGO) }
    )
    SideEffect { permissionsLauncher.launch(micPermission) }
}