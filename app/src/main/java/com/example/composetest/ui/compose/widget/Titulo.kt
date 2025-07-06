package com.example.composetest.ui.compose.widget

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.composetest.R
import com.example.composetest.ui.compose.theme.Tema

@Composable
fun Titulo(
    texto: String,
    modifier: Modifier? = null,
    nivel: NivelTitulo = NivelTitulo.Nivel1,
    textAlign: TextAlign? = null,
    color: Color = nivel.color,
    useMarquee: Boolean = false,
) {
    var maxLines by remember { mutableIntStateOf(Int.MAX_VALUE.takeUnless { useMarquee } ?: 1) }
    var marquee by remember { mutableStateOf(false) }
    var overflow by remember { mutableStateOf(TextOverflow.Visible) }

    Text(
        texto,
        modifier = (modifier ?: nivel.modifier)
            .then(Modifier.basicMarquee().takeIf { marquee } ?: Modifier),
        color = color,
        fontSize = nivel.fontSize,
        fontWeight = nivel.fontWeight,
        fontFamily = FontFamily(Font(R.font.eczar)),
        textAlign = textAlign ?: nivel.textAlign,
        onTextLayout = {
            if (useMarquee && it.didOverflowHeight) {
                marquee = true
                maxLines = 1
                overflow = TextOverflow.Clip
            }
        },
        maxLines = maxLines,
        overflow = overflow
    )
}

data class NivelTitulo(
    val fontSize: TextUnit,
    val fontWeight: FontWeight,
    val color: Color,
    val modifier: Modifier = Modifier,
    val textAlign: TextAlign = TextAlign.Unspecified,
) {

    companion object {
        private val textColor: Color
            @Composable get() = TituloDefaults.textColor

        val Nivel1: NivelTitulo
            @Composable get() = NivelTitulo(30.sp, FontWeight.Normal, textColor)

        val Nivel2: NivelTitulo
            @Composable get() = NivelTitulo(20.sp, FontWeight.Bold, textColor)

        val Nivel3: NivelTitulo
            @Composable get() =NivelTitulo(16.sp, FontWeight.Bold, textColor)

        val TituloPantalla: NivelTitulo
            @Composable get() = Nivel1.copy(
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Tema.colors.toolbarContent
            )

        val Pantalla : NivelTitulo
            @Composable get() = NivelTitulo(50.sp, FontWeight.Bold, Tema.colors.tituloInicioRonda)

        val SubtituloPantalla : NivelTitulo
            @Composable get() = NivelTitulo(44.sp, FontWeight.Bold, Tema.colors.subtituloInicioRonda)
    }
}

class TituloDefaults {
    companion object {
        val textColor
            @Composable get() = Tema.colors.texto
    }
}