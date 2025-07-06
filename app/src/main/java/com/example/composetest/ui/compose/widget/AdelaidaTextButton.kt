package com.example.composetest.ui.compose.widget

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetest.ui.compose.screen.PreviewFondo
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron

@Composable
fun AdelaidaTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonColors: ButtonColors = AdelaidaTextButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit = { Text("Adelaida Text Button") },
) {
    TextButton(
        onClick = onClick,
        colors = buttonColors,
        shape = RectangleShape,
        modifier = modifier,
        enabled = enabled,
        content = content
    )
}

class AdelaidaTextButtonDefaults() {

    companion object {
        val contentColor: Color = Color.Unspecified
        val disabledContentColor = Color.Unspecified

        @Composable
        fun buttonColors(
            contentColor: Color = Companion.contentColor,
            disabledContentColor: Color = Companion.disabledContentColor
        ): ButtonColors = ButtonDefaults.textButtonColors(
            contentColor = contentColor,
            disabledContentColor = disabledContentColor
        )
    }
}

@Preview
@Composable
private fun PreviewButton() {
    PreviewFondo {
        AdelaidaTextButton({}, enabled = true)
    }
}