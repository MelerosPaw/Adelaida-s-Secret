package com.example.composetest.ui.compose

import BotonDialogo
import Estilo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.R
import com.example.composetest.ui.compose.screen.PreviewComponente
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.screen.ScreenPreviewVerde
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.widget.AdelaidaButton
import com.example.composetest.ui.compose.widget.AdelaidaIconButton
import com.example.composetest.ui.compose.widget.AdelaidaIconDefaults
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.compose.widget.DialogTextCheckbox
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.NightAndDay
import com.example.composetest.ui.compose.widget.TextCheckbox
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.compose.widget.dialog.AdelaidaDialog
import com.example.composetest.ui.viewmodel.FiltrosViewModel

@Composable
fun Filtros(
    onFiltroCambiado: (FiltrosViewModel.EstadoFiltros) -> Unit,
    modo: Modo = Modo.Integrado(false),
) {
    val viewModel: FiltrosViewModel = hiltViewModel()
    viewModel.onEstadoCambiado = onFiltroCambiado

    when (modo) {
        is Modo.Integrado -> ModoIntegrado(modo, viewModel)
        is Modo.Dialogo -> ModoDialogo(modo, viewModel)
    }
}

@Composable
fun ModoIntegrado(modo: Modo.Integrado, viewModel: FiltrosViewModel) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = MargenEstandar)) {
        var estaAbierto by remember { mutableStateOf(modo.abierto) }

        BotonDialogo("Filtros", estilo = Estilo.Mayusculas(true), onClick = { estaAbierto = !estaAbierto })

        if (estaAbierto) {
            Ninguno(viewModel, Modo.Integrado(false))
            ControlesFiltros(viewModel, modo)
        }
    }
}

@Composable
fun ModoDialogo(modo: Modo.Dialogo, viewModel: FiltrosViewModel) {
    if (modo.abierto) {
        AdelaidaDialog(modo.onDismiss, DialogProperties()) {
            Box(Modifier.fillMaxWidth()) {
                Titulo("Filtros", nivel = NivelTitulo.Nivel2, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
                Ninguno(viewModel, modo, modifier = Modifier.align(Alignment.CenterEnd))
            }
            ControlesFiltros(viewModel, modo)
            BotonDialogo("Aplicar", estilo = Estilo.Mayusculas(false), onClick = modo.onDismiss)
        }
    }
}

@Composable
private fun Ninguno(viewModel: FiltrosViewModel, modo: Modo, modifier: Modifier = Modifier) {
    val hayFiltros by remember { viewModel.puedeResetearFiltros }

    when (modo) {
        is Modo.Dialogo -> AdelaidaIconButton(Icons.Filled.Delete, "Limpiar filtros", modifier, hayFiltros) { viewModel.resetearFiltros() }
        is Modo.Integrado -> AdelaidaButton({ viewModel.resetearFiltros() }, Modifier.fillMaxWidth(), hayFiltros) {
            AdelaidaText(text = "NINGUNO")
        }
    }
}

@Composable
private fun ControlesFiltros(viewModel: FiltrosViewModel, modo: Modo) {
    Column {
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Pistas(viewModel, modo)
            Cartas(viewModel, modo)
        }
    }
}

@Composable
private fun Pistas(viewModel: FiltrosViewModel, modo: Modo) {
    Column {
        viewModel.filtrosDePistas.forEach {
            Opcion(it, modo)
        }
    }
}

@Composable
private fun Cartas(viewModel: FiltrosViewModel,modo: Modo) {
    Column {
        viewModel.filtrosDeCartas.forEach {
            Opcion(it, modo)
        }
    }
}

@Composable
private fun Opcion(filtro: FiltrosViewModel.FiltroWrapper, modo: Modo) {
    when (modo) {
        is Modo.Integrado -> TextCheckbox(filtro.nombre, filtro.estado.value, filtro::modificacion.get())
        is Modo.Dialogo -> DialogTextCheckbox(filtro.nombre, filtro.estado.value, filtro::modificacion.get())
    }
}

@NightAndDay
@Composable
private fun PreviewModoDialogo() {
    ScreenPreviewMarron {
        Filtros({}, Modo.Dialogo(true) {})
    }
}

@NightAndDay
@Composable
private fun PreviewModoDialogoVerde() {
    ScreenPreviewVerde {
        Filtros({}, Modo.Dialogo(true) {})
    }
}

@NightAndDay
@Composable
private fun PreviewModoIntegrado() {
    PreviewComponente {
        Filtros({}, Modo.Integrado(true))
    }
}

sealed class Modo(val abierto: Boolean) {

    /** El botón "Filtros" es quien gestiona si está abierto o no. */
    class Integrado(abierto: Boolean = false) : Modo(abierto)

    /**
     * @property onDismiss Implementa esto para cambiar el estado que mantiene abierto el cuadro de
     * diálogo.
     */
    class Dialogo(abierto: Boolean, val onDismiss: () -> Unit) : Modo(abierto)
}

@Composable
fun BotonFiltros(tint: Color = AdelaidaIconDefaults.tint, onClicked: () -> Unit) {
    AdelaidaIconButton(painterResource(R.drawable.filter), "Filtros", tint = tint, onClick = onClicked)
}