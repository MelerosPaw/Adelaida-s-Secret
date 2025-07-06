package com.example.composetest.ui.compose.widget.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.composetest.R
import com.example.composetest.ui.compose.modifiers.ninePatchBackground
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.screen.ScreenPreviewVerde
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.AdelaidaText

private val CONTENT_COLOR
    @Composable get() = Tema.colors.contenidoDialogos

/**
 * @param contentMustScroll Por defecto, si el contenido no cabe en el alto de la pantalla, tendrá
 * scroll vertical, pero si el contenido tiene una LazyColumn, debes mandar `false` porque si no,
 * no se podrá pintar.
 * @param fullWidthHorizontalPadding Si usas `DialogProperties(usePlatformDefaultWidth = true),
 * necesitarás establecer un padding horizontal porque, de lo contrario, el ancho ocupará el total
 * de la pantalla.
 * @param fillMaxHeight Por defecto los cuadros de diálogo tiene una altura wrap_content, como
 * cualquier otro composable. Si pasas true, será fillMaxHeight porque se le aplicará al composable
 * raíz del Dialog. Si no, por mucho que sus hijos dijeran fillMaxHeight, sería dentro de esa
 * limitación del padre. (Creo).
 */
@Composable
fun AdelaidaDialog(
    onDismiss: () -> Unit,
    dialogProperties: DialogProperties = DialogProperties(false, false),
    contentPadding: Boolean = true,
    contentMustScroll: Boolean = true,
    fullWidthHorizontalPadding: Dp = 16.dp,
    fillMaxHeight: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismiss, dialogProperties) {
        val horizontalPadding = Modifier.takeIf { dialogProperties.usePlatformDefaultWidth }
            ?: Modifier.padding(fullWidthHorizontalPadding)
        Card(
            shape = RectangleShape,
            colors = CardDefaults.cardColors(containerColor = Tema.colors.fondoDialogos),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .then(Modifier.fillMaxHeight().takeIf { fillMaxHeight } ?: Modifier.wrapContentHeight())
                .then(horizontalPadding)
        ) {
            val scrollable = if (contentMustScroll) {
                val scrollState = rememberScrollState()
                Modifier.verticalScroll(scrollState)
            } else {
                Modifier
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .then(Modifier.fillMaxWidth().takeUnless { fillMaxHeight } ?: Modifier.fillMaxSize())
                    .padding(4.dp, 6.dp, 6.dp, 6.dp)
                    .ninePatchBackground(R.drawable.contorno_dialogos, CONTENT_COLOR.toArgb())
                    .padding((16.takeIf { contentPadding } ?: 0).dp)
                    .then(scrollable),
                content = content
            )
        }
    }
}

@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Marron() {
    ScreenPreviewMarron {
        AdelaidaDialog({}) {
            AdelaidaText("Cuadro de diálogo solo con mensaje", color = CONTENT_COLOR)
        }
    }
}

@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Verde() {
    ScreenPreviewVerde {
        AdelaidaDialog({}) {
            AdelaidaText("Cuadro de diálogo solo con mensaje", color = CONTENT_COLOR)
        }
    }
}

@Preview
@Composable
fun DialogOrdinario() {
    Dialog({}) {
        Card(
            shape = RectangleShape,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Título", style = MaterialTheme.typography.titleMedium)
                Text("Mensaje", Modifier.padding(top = 16.dp), style = MaterialTheme.typography.bodyMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton({ /* OnClick positivo*/ }) { Text("SÍ", fontWeight = FontWeight.Bold)}
                    TextButton({ /* OnClick negativo*/ }) { Text("NO", fontWeight = FontWeight.Bold)}
                }
            }
        }
    }
}