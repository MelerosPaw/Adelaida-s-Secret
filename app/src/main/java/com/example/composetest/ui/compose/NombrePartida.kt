package com.example.composetest.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.ui.compose.dialog.DialogoCambioNombrePartida
import com.example.composetest.ui.compose.modifiers.onLongClick
import com.example.composetest.ui.compose.theme.TextoPresionado
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.compose.widget.TituloDefaults
import com.example.composetest.ui.viewmodel.NombrePartidaViewModel

@Composable
fun NombrePartida(
    nombre: String?,
    alCambiarElNombre: (String) -> Unit,
    modifier: Modifier = Modifier,
    creando: Boolean = false,
    enabled: Boolean = true,
    tint: Color = TituloDefaults.textColor,
) {
    val viewModel = hiltViewModel<NombrePartidaViewModel>()
    viewModel.setNombre(nombre, !creando)

    Nombre(viewModel, enabled, NivelTitulo.TituloPantalla.modifier.then(modifier), tint)
    Dialogo(viewModel, {
        viewModel.onNombreCambiado(it)
        alCambiarElNombre(it)
    }, !creando)
}

@Composable
private fun Nombre(
    viewModel: NombrePartidaViewModel,
    enabled: Boolean,
    modifier: Modifier,
    color: Color,
) {
    val nombrePartida by remember { viewModel.nombreEnElTitulo }
    val colorPorDefecto = color
    var colorTexto by remember { mutableStateOf(colorPorDefecto) }

    fun onTituloPresionado(estaPresionado: Boolean) {
        colorTexto = TextoPresionado.takeIf { estaPresionado } ?: colorPorDefecto
    }

    Titulo(
        nombrePartida,
        nivel = NivelTitulo.TituloPantalla,
        modifier = Modifier
            .onLongClick(onBeingLongPressed = ::onTituloPresionado, enabled = enabled) {
                viewModel.mostrarDialogoCambioNombre()
            }
            .then(modifier),
        color = colorTexto,
        useMarquee = true
    )
}

@Composable
private fun Dialogo(
    viewModel: NombrePartidaViewModel,
    alCambiarElNombre: (String) -> Unit,
    isDebug: Boolean
) {
    val estaEditandoNombre by remember { viewModel.mostrarDialogoCambioNombre }
    val nombreEnElTitulo by remember { viewModel.nombreEnElTitulo }

    if (estaEditandoNombre) {
        DialogoCambioNombrePartida("Cambiar nombre", nombreEnElTitulo,
            alCambiarElNombre, viewModel::cancelarCambioNombre, isDebug
        )
    }
}

