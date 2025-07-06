package com.example.composetest.ui.compose.widget

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetest.ui.compose.screen.PreviewFondo
import com.example.composetest.ui.compose.screen.PreviewFondoVerde

@Composable
fun AdelaidaIconButton(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconModifier: Modifier = Modifier,
    tint: Color = AdelaidaIconDefaults.tint,
    onClick: () -> Unit,
) {
    IconButton(onClick, modifier, enabled) {
        AdelaidaIcon(imageVector, contentDescription, iconModifier, tint)
    }
}

@Composable
fun AdelaidaIconButton(
    painter: Painter,
    contentDescription: String?,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    tint: Color = AdelaidaIconDefaults.tint,
    onClick: () -> Unit,
) {
   AdelaidaIcon(
       painter,
       contentDescription,
       modifier.clickable(onClick = onClick, enabled = enabled),
       tint
   )
}

@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun P1() {
    PreviewFondo {
        AdelaidaIconButton(Icons.Default.Start, "Descripción") { }
    }
}

@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun P2() {
    PreviewFondoVerde {
        AdelaidaIconButton(Icons.Default.Start, "Descripción") { }
    }
}