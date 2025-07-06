package com.example.composetest.ui.compose.widget.dialog

import BotonDialogo
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.composetest.model.ElementoTablero
import com.example.composetest.ui.compose.ElementoMano
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.theme.TextoClaro
import com.example.composetest.ui.compose.theme.displayFontFamily
import com.example.composetest.ui.compose.widget.AdelaidaText

@Composable
fun <T> AdelaidaButtonDialog(
    mensaje: String,
    opciones: Array<OpcionDialogo<T>>,
    elementoTablero: ElementoTablero,
    spans: List<AnnotatedString.Range<SpanStyle>> = emptyList(),
    colorTexto: Color = AdelaidaButtonDialogDefaults.colorTexto,
    fontWeight: FontWeight = AdelaidaButtonDialogDefaults.fontWeight,
    fullWidth: Boolean = false,
    onDismiss: () -> Unit = { /* No se puede cerrar sin realizar una acción. */ },
) {
    AdelaidaButtonDialog(mensaje, opciones,
        {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.wrapContentSize()) {
                Box(Modifier.padding(top = 6.dp).background(color = Color.White, shape = CircleShape).size(145.dp))
                ElementoMano(
                    elementoTablero,
                    conPadding = false,
                    colorTexto = TextoClaro,
                    decorativo = true,
                )
            }
        }, colorTexto, fontWeight, spans, fullWidth, onDismiss)
}

@Composable
fun <T> AdelaidaButtonDialog(
    mensaje: String,
    opciones: Array<OpcionDialogo<T>>,
    cabecera: @Composable ColumnScope.() -> Unit = {},
    colorTexto: Color = AdelaidaButtonDialogDefaults.colorTexto,
    fontWeight: FontWeight = AdelaidaButtonDialogDefaults.fontWeight,
    spans: List<AnnotatedString.Range<SpanStyle>> = emptyList(),
    fullWidth: Boolean = false,
    onDismiss: () -> Unit = { /* No se puede cerrar sin realizar una acción. */ },
) {
    AdelaidaDialog(onDismiss, DialogProperties(false, false, !fullWidth)) {
        cabecera.invoke(this)

        AdelaidaText(
            mensaje, fontFamily = displayFontFamily, fontWeight = fontWeight, spans = spans,
            fontSize = 16.sp, modifier = Modifier.padding(bottom = MargenEstandar), color = colorTexto
        )

        Opciones<T>(opciones)
    }
}

@Composable
private fun <T> Opciones(opciones: Array<OpcionDialogo<T>>) {
    opciones.forEach {
        BotonDialogo(it.texto, it.enabled) { it.onClick(it.valor) }
    }
}

@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMultibuttonDialog() {
    ScreenPreviewMarron {
        AdelaidaButtonDialog(
            "¿Quieres hacer lo que vas a hacer?",
            arrayOf(
                OpcionDialogo("Of course, guapi", null, false) {},
                OpcionDialogo("Of course que no, guapi", null) {},
                OpcionDialogo("¿Algo más?", null) {}
            ),
            elementoTablero = ElementoTablero.Pista.Secreto(9),
        )
    }
}

class OpcionDialogo<T>(
    val texto: String,
    val valor: T,
    val enabled: Boolean = true,
    val onClick: (T) -> Unit,
)

class AdelaidaButtonDialogDefaults {

    companion object {
        val colorTexto: Color
            @Composable get() = Tema.colors.contenidoDialogos
        val fontWeight = FontWeight.Bold
    }
}