package com.example.composetest.ui.compose.dialog

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.ui.compose.widget.dialog.AdelaidaTextFieldDialog
import com.example.composetest.ui.viewmodel.DialogoNombrePartidaViewModel

@Composable
fun DialogoCambioNombrePartida(
    mensaje: String,
    nombreOriginal: String?,
    onAceptar: (String) -> Unit,
    onCancelar: () -> Unit,
    isDebug: Boolean
) {
    val vm = hiltViewModel<DialogoNombrePartidaViewModel>()
    vm.inicializar(nombreOriginal, isDebug)

    Dialogo(vm, mensaje, nombreOriginal, onAceptar, onCancelar)
}

@Composable
fun Dialogo(
    vm: DialogoNombrePartidaViewModel,
    mensaje: String,
    nombreOriginal: String?,
    onAceptar: (String) -> Unit,
    onCancelar: () -> Unit,
) {
    val nombre by remember { vm.textoEnCampo }
    val error by remember { vm.error }

    AdelaidaTextFieldDialog(
        mensaje,
        "Nombre de la partida",
        nombre, vm::onCampoCambiado,
        "Aceptar", { vm.cambiarNombrePartida(nombre, nombreOriginal, onCancelar, onAceptar) },
        "Cancelar", onCancelar,
        error
    )
}

@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDialogo() {
    DialogoCambioNombrePartida("¡Pon aquí un puto nombre, joder!", "Amargura", {}, {}, false)
}