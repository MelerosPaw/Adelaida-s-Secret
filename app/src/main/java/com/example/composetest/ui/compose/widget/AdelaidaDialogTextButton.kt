package com.example.composetest.ui.compose.widget

import android.content.res.Configuration
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.screen.ScreenPreviewVerde
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.dialog.AdelaidaDialog

@Composable
fun AdelaidaDialogTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonColors: ButtonColors = AdelaidaDialogTextButtonDefaults.buttonColors(contentColor = Color.White),
    content: @Composable RowScope.() -> Unit = { Text("Adelaida Text Button") },
) {
    AdelaidaTextButton(onClick, modifier, enabled, buttonColors, content)
}

class AdelaidaDialogTextButtonDefaults() {

    companion object {
        val contentColor: Color
            @Composable get() = Tema.colors.contenidoDialogos
        val disabledContentColor
            @Composable get() = Tema.colors.textoBotonInhabilitado

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

@Preview(name = "Claro inhabilitado")
@Preview(name = "Oscuro inhabilitado", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun P3() {
    ScreenPreviewMarron {
        AdelaidaDialog({}) {
            AdelaidaDialogTextButton({}, enabled = true)
            AdelaidaDialogTextButton({}, enabled = false)
        }
    }
}

@Preview(name = "Verde claro inhabilitado")
@Preview(name = "Verde oscuro inhabilitado", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun P4() {
    ScreenPreviewVerde {
        AdelaidaDialog({}) {
            AdelaidaDialogTextButton({}, enabled = true)
            AdelaidaDialogTextButton({}, enabled = false)
        }
    }
}