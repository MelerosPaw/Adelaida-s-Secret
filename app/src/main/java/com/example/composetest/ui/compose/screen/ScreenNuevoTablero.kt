package com.example.composetest.ui.compose.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.R
import com.example.composetest.extensions.get
import com.example.composetest.extensions.joinToStringHumanReadable
import com.example.composetest.model.Sospechoso
import com.example.composetest.ui.compose.BotonFiltros
import com.example.composetest.ui.compose.Cargando
import com.example.composetest.ui.compose.HtmlSpan
import com.example.composetest.ui.compose.Modo
import com.example.composetest.ui.compose.TableroConControles
import com.example.composetest.ui.compose.navegacion.CLAVE_CARGA_INICIAL
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.navegacion.NavegadorCreacion.ConfiguracionToolbar
import com.example.composetest.ui.compose.navegacion.PartidaModelo
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.MargenSuperiorTituloToolbar
import com.example.composetest.ui.compose.widget.AdelaidaButton
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.viewmodel.NuevoTableroViewModel
import com.example.composetest.ui.viewmodel.TableroViewModel

@Composable
fun ScreenNuevoTablero(
    onNavegarASiguientePaso: (PartidaModelo) -> Unit,
    onMensaje: (Mensaje) -> Unit
) {
    val viewModel: NuevoTableroViewModel = hiltViewModel()
    viewModel.onMensaje = onMensaje

    LaunchedEffect(CLAVE_CARGA_INICIAL) {
        if (viewModel.noTieneTablero()) {
            viewModel.cargarTablero()
        }
    }
    val tablero by remember { viewModel.tablero }

    Screen(
        configuracionToolbar = ConfiguracionToolbar(
            titulo = ConfiguracionToolbar.titulo("Tablero"),
            actions = { AbrirFiltros(tablero != null, viewModel::abrirFiltros) }
        )
    ) {
        Box {
            val scrollState = rememberScrollState()
            Column(Modifier.verticalScroll(scrollState).padding(top = MargenSuperiorTituloToolbar)) {
                Explicacion()
                Asesino(viewModel)
                NuevoTablero(viewModel)
                Comenzar(viewModel)
                Tablero(viewModel)
                DialogoConfirmarInicio(viewModel, onNavegarASiguientePaso)
            }

            CargandoCrearTablero(viewModel)
        }
    }
}

@Composable
fun AbrirFiltros(mostrar: Boolean, abrirFiltros: () -> Unit) {
    if (mostrar) {
        BotonFiltros(onClicked = abrirFiltros)
    }
}

@Composable
private fun Explicacion() {
    val tableroNuevo = R.string.titulo_nuevo_tablero.get()
    val finalizar = R.string.boton_finalizar_nuevo_tablero.get()
    val html = HtmlSpan(R.string.explicacion_tablero.get(tableroNuevo, tableroNuevo, finalizar))

    AdelaidaText(
        html.text, spans = html.spans,
        modifier = Modifier.padding(horizontal = MargenEstandar))
}

@Composable
private fun Asesino(viewModel: NuevoTableroViewModel) {
    val asesino by remember { viewModel.asesino }

    asesino?.let {
        val pistasAExcluir = it.identificadoresPistas().joinToStringHumanReadable { it }
        val idTextoAsesino = when (it.genero) {
            Sospechoso.Genero.HOMBRE -> R.string.explicacion_asesino
            Sospechoso.Genero.MUJER -> R.string.explicacion_asesina
        }
        val textoAsesino = HtmlSpan(idTextoAsesino.get(it.nombre, pistasAExcluir))

        AdelaidaText(textoAsesino.text, spans = textoAsesino.spans,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MargenEstandar)
                .padding(top = MargenEstandar)
        )
    }
}

@Composable
private fun NuevoTablero(viewModel: NuevoTableroViewModel) {
    AdelaidaButton(
        onClick = { viewModel.nuevoTablero() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MargenEstandar)
            .padding(top = MargenEstandar),
    ) {
        AdelaidaText(text = "TABLERO NUEVO")
    }
}

@Composable
private fun Comenzar(viewModel: NuevoTableroViewModel) {
    val puedeComenzar by remember { viewModel.puedeComenzar }

    AdelaidaButton(
        enabled = puedeComenzar,
        onClick = { viewModel.mostrarConfirmacionInicio() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MargenEstandar)
    ) {
        AdelaidaText(R.string.boton_finalizar_nuevo_tablero.get().uppercase())
    }
}

@Composable
private fun DialogoConfirmarInicio(
    viewModel: NuevoTableroViewModel,
    onNavegarASiguientePaso: (partida: PartidaModelo) -> Unit
) {
    val mostrarConfirmacionInicio by remember { viewModel.mostrarDialogoComenzar }

    if (mostrarConfirmacionInicio) {
        AdelaidaButtonDialog("¿Está el tablero montado ya?",
            arrayOf(
                OpcionDialogo("Sí, continuemos", null) { viewModel.guardarTableroYComenzar(onNavegarASiguientePaso) },
                OpcionDialogo("Todavía no", null) { viewModel.ocultarConfirmacionInicio() }
            ),
            onDismiss = { viewModel.ocultarConfirmacionInicio() }
        )
    }
}

@Composable
private fun Tablero(viewModel: NuevoTableroViewModel) {
    val tablero by remember { viewModel.tablero }
    val estadoFiltros by remember { viewModel.estadoFiltros }

    tablero?.let {
        val tableroViewModel: TableroViewModel = hiltViewModel()
        tableroViewModel.inicializar(viewModel.idPartida, it, emptyList(), false, null)
        TableroConControles(null, true, true, true,
            Modo.Dialogo(estadoFiltros, viewModel::cerrarFiltros),
            { /* no-op. No se puede clicar, por lo que no existen acciones prohibidas. */ })
    }
}

@Composable
private fun CargandoCrearTablero(viewModel: NuevoTableroViewModel) {
    val cargando by remember { viewModel.estaCargando }

    Box(Modifier.fillMaxSize(), propagateMinConstraints = true) {
        Cargando(cargando)
    }
}

@Preview
@Composable
fun PreviewNuevoTablero() {
    ScreenPreviewMarron {
        ScreenNuevoTablero({}, {})
    }
}