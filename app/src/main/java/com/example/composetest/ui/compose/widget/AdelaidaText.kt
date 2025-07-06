package com.example.composetest.ui.compose.widget

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetest.R
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.theme.bodyFontFamily

/**
 * @param inicialDecorativa Pone en un tamaño de letra mayor la primera letra del texto. Si el
 * primer carácter no es una letra, aplica la transformación a todos los caracteres iniciales hasta
 * encontrar el primero que sea una letra. Por ejemplo, en el texto _-La palabra_, pondrá en
 * letra decorativa tanto el guion como la letra _l_.
 */
@Composable
fun AdelaidaText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = AdelaidaTextDefaults.fontSize,
    fontWeight: FontWeight = AdelaidaTextDefaults.fontWeight,
    textAlign: TextAlign = TextAlign.Unspecified,
    color: Color = AdelaidaTextDefaults.color,
    spans: List<AnnotatedString.Range<SpanStyle>> = emptyList(),
    inicialDecorativa: Boolean = false,
    fontFamily: FontFamily = AdelaidaTextDefaults.font,
    fontStyle: FontStyle = AdelaidaTextDefaults.fontStyle,
) {
    val texto = text.takeIf { spans.isEmpty() } ?: AnnotatedString(text, spans)

    val textoFinal: AnnotatedString = if (inicialDecorativa) {
        buildAnnotatedString {
            val firstLetterIndex = texto.indexOfFirst { it.isLetter() }
            LetraGrande(texto.subSequence(0, firstLetterIndex + 1).toString())
            append(texto, firstLetterIndex + 1, texto.length)
        }
    } else {
        buildAnnotatedString { append(texto) }
    }

    Text(
        textoFinal,
        modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textAlign = textAlign,
        overflow = TextOverflow.Clip,
    )
}

@Composable
private fun AnnotatedString.Builder.LetraGrande(texto: String) {
    withStyle(
        SpanStyle(
            fontSize = 60.sp,
            fontFamily = FontFamily(Font(R.font.eczar))
        )
    ) {
        append(texto)
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF5D4B4B)
fun Preview() {
    Box(Modifier.size(30.dp)) {
        AdelaidaText("Texto de prueba\ncon dos líneas")
    }
}

class AdelaidaTextDefaults {

    companion object {
        val color: Color
            @Composable get() = Tema.colors.texto
        val fontWeight: FontWeight = FontWeight.Normal
        val font: FontFamily = bodyFontFamily
        val fontStyle: FontStyle = FontStyle.Normal
        val fontSize: TextUnit = 16.sp
    }
}

@Composable
fun AdelaidaButtonText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = AdelaidaTextDefaults.fontSize,
    fontWeight: FontWeight = AdelaidaTextDefaults.fontWeight,
    textAlign: TextAlign = TextAlign.Unspecified,
    color: Color = Color.Unspecified,
    spans: List<AnnotatedString.Range<SpanStyle>> = emptyList(),
    inicialDecorativa: Boolean = false,
    fontFamily: FontFamily = AdelaidaTextDefaults.font,
) {
    AdelaidaText(text, modifier, fontSize, fontWeight, textAlign, color, spans, inicialDecorativa,
        fontFamily)
}