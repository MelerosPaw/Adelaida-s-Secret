package com.example.composetest.ui.compose.modifiers

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.resalte(color: Color, resaltado: Boolean, size: DpSize? = null): Modifier = composed {
    val color = color.takeIf { resaltado } ?: Color.Transparent
    val modSize = size?.let { Modifier.size(size) } ?: Modifier.wrapContentSize()

    Modifier
        .then(modSize)
        .border(4.dp, color)
        .padding(10.dp)
        .border(2.dp, color)
        .padding(10.dp)
}