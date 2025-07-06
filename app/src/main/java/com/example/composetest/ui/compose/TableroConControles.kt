package com.example.composetest.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.dialog.ConjuntoOpciones
import com.example.composetest.ui.compose.dialog.DialogoElementoClicado
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.viewmodel.TableroViewModel

/**
 * @param wrapContent Si es `true`, sirve para estar dentro de un scroll. Si no, ocupará todo lo que
 * tenga disponible de pantalla y tendrá un scroll en sí mismo.
 */
@Composable
fun TableroConControles(
    jugadores: List<Jugador>?,
    wrapContent: Boolean = true,
    puedeMostrarEsquema: Boolean = true,
    puedeMostrarBotonCasillasVacias: Boolean = false,
    modo: Modo,
    onAccionProhibida: (AccionProhibida) -> Unit
) {
    val viewModel: TableroViewModel = hiltViewModel()
    val boxModifier = if (wrapContent) {
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    } else {
        Modifier.fillMaxSize()
    }

    Box(boxModifier, propagateMinConstraints = true) {
        val scroll = rememberScrollState()
        val modifiers = Modifier.takeIf { wrapContent } ?: Modifier.verticalScroll(scroll)

        Column(modifiers) {
            val impresora by remember { viewModel.impresoraState }

            impresora?.let {
                Spacer(Modifier.height(MargenEstandar))
                Box(Modifier.padding(end = MargenEstandar, start = 8.dp)) {
                    Tablero(viewModel, it)
                }
                Controles(impresora?.tablero?.hayCasillasVacias() == true, puedeMostrarEsquema,
                    puedeMostrarBotonCasillasVacias)
                Filtros({ viewModel.onFiltrosCambiados(it) }, modo)
                EsquemaTablero(viewModel)
            }
        }

        val estaCargando by remember { viewModel.estaCargando }
        CuadroDialogoElementoClicado(viewModel, jugadores, onAccionProhibida)

        if (estaCargando) {
            Cargando()
        }
    }
}

@Composable
private fun CuadroDialogoElementoClicado(
    viewModel: TableroViewModel,
    jugadores: List<Jugador>?,
    onAccionProhibida: (AccionProhibida) -> Unit
) {
    val elementoClicado by remember { viewModel.elementoClicado }

    noneNull(elementoClicado, jugadores, viewModel.idPartida) { elemento, jugadores, idPartida ->
        DialogoElementoClicado(elemento.elemento, jugadores, elemento.poseedor, ConjuntoOpciones.TABLERO, idPartida,
            onAccionProhibida) {
            viewModel.cerrarDialogCasillaClicada()
        }
    }
}