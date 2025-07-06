package com.example.composetest.ui.compose.widget

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.composetest.ui.compose.theme.Tema

@Composable
fun AdelaidaIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = AdelaidaIconDefaults.tint,
) {
    Icon(imageVector, contentDescription, modifier, tint)
}

@Composable
fun AdelaidaIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = AdelaidaIconDefaults.tint,
) {
    Icon(painter, contentDescription, modifier, tint)
}

class AdelaidaIconDefaults {

    companion object {
        val tint
            @Composable get() = Tema.colors.iconos
    }
}