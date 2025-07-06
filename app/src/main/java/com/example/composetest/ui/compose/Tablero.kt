package com.example.composetest.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.composetest.R
import com.example.composetest.model.Tablero
import com.example.composetest.model.aCaracter
import com.example.composetest.ui.CasillaVO
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.viewmodel.TableroViewModel

@Composable
fun Tablero(viewModel: TableroViewModel, impresora: Tablero.Impresora) {
    Column {
        repeat(impresora.tablero.cantidadFilas + 1) { fila ->
            if (fila == 0) {
                FilaEncabezados(impresora.tablero.cantidadColumnas)
            } else {
                val casillas = viewModel.getCasillasEnFila(impresora.tablero, fila)
                FilaCasillas(fila, casillas, viewModel)
            }
        }
    }
}

@Composable
private fun FilaEncabezados(cantidadColumnas: Int) {
    Row {
        repeat(cantidadColumnas + 1) {
            ColumnaEncabezados(it)
        }
    }
}

@Composable
private fun RowScope.ColumnaEncabezados(posicion: Int) {
    if (posicion == 0) {
        TextoCentrado(
            texto = "A",
            Modifier
                .height(30.dp)
                .alpha(0f), false, false
        )
    } else {
        TextoCentrado(texto = "$posicion", mantenerAlto = false, mantenerAncho = true,
            fontFamily = FontFamily(Font(R.font.eczar)))
    }
}

@Composable
private fun FilaCasillas(
    numeroFila: Int,
    casillasEnLaFila: Array<CasillaVO>,
    viewModel: TableroViewModel,
) {
    Row {
        CabeceraFila(casillasEnLaFila.firstOrNull()?.casilla?.fila ?: numeroFila.aCaracter())
        casillasEnLaFila.forEach {
            Casilla(it, viewModel)
        }
    }
}

@Composable
private fun RowScope.CabeceraFila(id: Char) {
    TextoCentrado(texto = id.toString(), mantenerAlto = true, mantenerAncho = false,
        fontFamily = FontFamily(Font(R.font.eczar)))
}

@Composable
private fun RowScope.Casilla(casilla: CasillaVO, viewModel: TableroViewModel) {
    val impresora by remember { viewModel.impresoraState }

    impresora?.let {
        val accionAlClicar: Modifier = casilla.casilla.contenido
            ?.takeIf { viewModel.puedeClicarCasillas && casilla.puedeMostrarse }
            ?.let { Modifier.clickable { viewModel.onCasillaClicada(casilla.casilla) } }
            ?: Modifier

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .background(viewModel.getColorDeCasilla(casilla.casilla))
                .then(accionAlClicar)
        ) {
            casilla.casilla.contenido
                ?.takeIf { viewModel.puedeMostrarseEnElTablero(it, casilla) }
                ?.let { elemento ->
                    ElementoMano(elemento, true, ElementoMano.Dimensiones.Reducido, it.usarIconos,
                        it.usarValores, casilla.contenidoEstaEnElTablero)
                }
        }
    }
}

@Composable
private fun RowScope.TextoCentrado(
    texto: String,
    modifier: Modifier = Modifier,
    mantenerAlto: Boolean = true,
    mantenerAncho: Boolean = true,
    fontFamily: FontFamily = FontFamily(Font(R.font.anaheim)),
) {
    var modifiers: Modifier = Modifier.then(Modifier.weight(1f))

    if (mantenerAlto && mantenerAncho) {
        modifiers = modifiers.then(Modifier.aspectRatio(1f))
    } else if (mantenerAlto) {
        modifiers = modifiers.align(Alignment.CenterVertically)
    } else {
        modifiers = modifiers.wrapContentHeight()
    }

    Box(modifier = modifiers.then(modifier)) {
        AdelaidaText(
            text = texto,
            fontFamily = fontFamily,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}