package com.example.composetest.ui.compose.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LazyGridItemScope.GridItem(
    separador: SeparadorGrid,
    content: @Composable LazyGridItemScope.() -> Unit
) {
    Row {
        separador.izquierda()?.let { Spacer(Modifier.width(it)) }

        Column(Modifier.weight(1f)) {
            separador.arriba()?.let { Spacer(Modifier.height(it)) }
            content()
            separador.abajo()?.let { Spacer(Modifier.height(it)) }
        }

        separador.derecha()?.let { Spacer(Modifier.width(it)) }
    }
}

class SeparadorGrid(
    private val columnas: Int,
    posicion: Int,
    totalItems: Int,
    val separadores: InfoSeparadores
) {

    private val par: Boolean = posicion % 2 == 0
    private val esPrimeraFila: Boolean = posicion < columnas
    private val ultimo: Boolean = posicion == totalItems - 1
    private val penultimo: Boolean = posicion == totalItems - 2

    fun derecha(): Dp? = separadores.right.takeIf { columnas == 2 && par }

    fun izquierda(): Dp? = separadores.left.takeIf { columnas == 2 && !par }

    fun abajo(): Dp? = separadores.bottom.takeIf {
        !ultimo && !(penultimo && columnas == 2 && par)
    }

    fun arriba(): Dp? = separadores.top.takeIf {
        columnas != 2 || !esPrimeraFila
    }

    class InfoSeparadores(
        val left: Dp = 0.dp,
        val top: Dp = 0.dp,
        val right: Dp = 0.dp,
        val bottom: Dp = 0.dp
    )
}