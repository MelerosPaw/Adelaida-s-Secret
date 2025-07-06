package com.example.composetest.ui.compose.modifiers

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity

fun Modifier.cuadrado(): Modifier = composed {
    var ancho by remember { mutableIntStateOf(0) }
    var alto by remember { mutableIntStateOf(0) }

    val anchoFinal = with(LocalDensity.current) {
        ancho.toDp()
    }
    val altoFinal = with(LocalDensity.current) {
        alto.toDp()
    }
    val modAncho = Modifier.width(anchoFinal).takeIf { ancho > 0 } ?: Modifier
    val modAlto = Modifier.height(altoFinal).takeIf { alto > 0 } ?: Modifier

    onGloballyPositioned {
        with(it.size) {
            if (height > width) {
                ancho = height
            } else if (width > height) {
                alto = width
            }
        }
    }
        .then(modAncho)
        .then(modAlto)
}