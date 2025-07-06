package com.example.composetest.ui.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.navegacion.Navegacion
import com.example.composetest.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val splashScreen = installSplashScreen()
    var mostrarSplashScreen = true

    splashScreen.setKeepOnScreenCondition { mostrarSplashScreen }

    val viewModel: MainViewModel by viewModels()
    habilitarFuncionesBase(viewModel)
    viewModel.datosInicialesLiveData.observe(this) {
      mostrarSplashScreen = false

      if (!it) {
        viewModel.mostrarMensaje(Mensaje("Datos iniciales no cargados"))
      }
    }

    viewModel.comprobaDatosIniciales()

    enableEdgeToEdge()
    setContent { Navegacion() }
  }

  private fun mostrarExplicacion() {
    AlertDialog.Builder(this)
      .setMessage("Si no concedes permiso para el micrófono, no puedes usar la función de grabación.")
      .setPositiveButton("Vale, te lo doy") { dialog, _ ->
        dialog.dismiss()
      }
      .setNegativeButton("No quiero", null)
      .setCancelable(false)
      .create()
      .show()
  }

  private fun habilitarGrabacion() {
//    viewModel.mostrarControlesGrabacion(true)
    Toast.makeText(this, "Puedes grabar audio", Toast.LENGTH_SHORT).show()
  }

  private fun inhabilitarGrabacion() {
    Toast.makeText(
      this, "No puedes grabar audio. Si quieres grabar, debes cambiar el permiso " +
              "de Micrófono desde Ajustes", Toast.LENGTH_SHORT
    ).show()
  }
}