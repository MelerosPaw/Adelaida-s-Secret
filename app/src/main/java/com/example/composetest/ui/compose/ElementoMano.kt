package com.example.composetest.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetest.R
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.ElementoTablero
import com.example.composetest.ui.compose.ElementoMano.Dimensiones
import com.example.composetest.ui.compose.theme.bodyFontFamily
import com.example.composetest.ui.compose.theme.displayFontFamily
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.compose.widget.AdelaidaTextDefaults

@Composable
fun ElementoMano(
    elemento: ElementoTablero,
    letraFija: Boolean = true,
    dimensiones: Dimensiones = Dimensiones.Grande,
    usarIcono: Boolean = true,
    usarValores: Boolean = true,
    estaEnElTablero: Boolean = true,
    conPadding: Boolean = true,
    decorativo: Boolean = false,
    invisible: Boolean = false,
    colorTexto: Color = AdelaidaTextDefaults.color
) {
    val (icono, texto) = definirIconoYTexto(elemento, usarIcono, usarValores)

    noneNull(icono, texto) { ic, txt ->
        val fontSize = calcularFontSize(letraFija, texto, dimensiones.divisor)
        val paddingTop = calcularPaddingTexto(letraFija, texto, dimensiones.divisor)
        val alpha = 1f.takeIf { estaEnElTablero } ?: 0.25f
        val paddingContenedor = if (conPadding) {
            Modifier.padding(10.dp / dimensiones.divisor)
        } else {
            Modifier
        }
        val textColor = Color.Transparent.takeIf { invisible } ?: colorTexto

        Box(
            contentAlignment = Alignment.TopCenter.takeIf { !decorativo } ?: Alignment.Center,
            modifier = paddingContenedor.alpha(alpha)
        ) {
            if (decorativo) {
                Image(
                    painterResource(elemento.getIconoDecorativo()),
                    "",
                    Modifier.size(96.dp / dimensiones.divisor)
                        .alpha(0f.takeIf { invisible } ?: 1f)
                )
            } else {
                AdelaidaText(
                    ic,
                    fontSize = 60.sp / dimensiones.divisor,
                    modifier = Modifier.padding(top = 8.dp)
                        .alpha(0f.takeIf { invisible } ?: 1f)
                )
            }

            AdelaidaText(
                txt, fontSize = fontSize,
                fontWeight = FontWeight.Black,
                fontFamily = displayFontFamily,
                color = textColor,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(top = 10.dp.takeIf { !decorativo } ?: 0.dp)
                    .padding(top = paddingTop.takeIf { !decorativo } ?: 80.dp)
            )
        }
    }
}

private fun definirIconoYTexto(
    elemento: ElementoTablero,
    usarIcono: Boolean,
    usarValor: Boolean,
): Pair<String?, String> {
    var icono = elemento.icono
    var texto = when (elemento) {
        is ElementoTablero.Pista -> elemento.valor.toString()
        is ElementoTablero.Carta.Dinero -> elemento.monedas.toString()
        is ElementoTablero.Carta -> elemento.prefijo.id
    }

    when {
        usarIcono && usarValor -> { /* Nos lo quedamos tal cual */ }
        usarIcono && !usarValor -> texto = ""
        !usarIcono && usarValor -> {
            icono = elemento.prefijo.id.takeIf { elemento !is ElementoTablero.Carta.Dinero }
                .orEmpty() +
                    texto.takeIf { elemento is ElementoTablero.Pista || elemento is ElementoTablero.Carta.Dinero }
                        .orEmpty()
            texto = ""
        }

        else -> {
            icono = elemento.prefijo.id
            texto = ""
        }
    }

    return Pair(icono, texto)
}

private fun calcularFontSize(
    letraFija: Boolean,
    texto: String,
    divider: Float
): TextUnit = if (letraFija) {
    48.sp
} else {
    when (texto.length) {
        1, 2 -> 48.sp
        3 -> 42.sp
        else -> 32.sp
    }
} / divider

private fun calcularPaddingTexto(
    letraFija: Boolean,
    texto: String,
    divider: Float
): Dp = if (letraFija) {
    30.dp
} else {
    when (texto.length) {
        1, 2 -> 30.dp
        3 -> 38.dp
        else -> 48.dp
    }
} / divider

@Preview
@Composable
fun PreviewElementoMano() {
    Row {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ElementoMano(ElementoTablero.Pista.Habito(3))
            ElementoMano(ElementoTablero.Pista.Objeto(7))
            ElementoMano(ElementoTablero.Pista.Testigo(4))
            ElementoMano(ElementoTablero.Pista.Coartada(5))
            ElementoMano(ElementoTablero.Pista.Secreto(2))
            ElementoMano(ElementoTablero.Pista.PistaFalsa(ElementoTablero.Pista.Prefijo.TESTIGO))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ElementoMano(ElementoTablero.Carta.Reputacion("Pedrito"))
            ElementoMano(ElementoTablero.Carta.Dinero(4, 1000))
            ElementoMano(ElementoTablero.Carta.Dinero(1, 100))
            ElementoMano(ElementoTablero.Carta.Llave(1))
            ElementoMano(ElementoTablero.Carta.Perseskud())
            ElementoMano(ElementoTablero.Carta.Brandy(2))
            ElementoMano(ElementoTablero.Carta.AcusacionExtra())
        }
    }
}

@Composable
@Preview
fun PreviewElementoManoDecorativo() {
    Row {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ElementoMano(ElementoTablero.Pista.Habito(3), decorativo = true)
            ElementoMano(ElementoTablero.Pista.Objeto(7), decorativo = true)
            ElementoMano(ElementoTablero.Pista.Testigo(4), decorativo = true)
            ElementoMano(ElementoTablero.Pista.Coartada(5), decorativo = true)
            ElementoMano(ElementoTablero.Pista.Secreto(2), decorativo = true)
            ElementoMano(ElementoTablero.Pista.PistaFalsa(ElementoTablero.Pista.Prefijo.TESTIGO), decorativo = true)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ElementoMano(ElementoTablero.Carta.Reputacion("Pedrito"), decorativo = true)
            ElementoMano(ElementoTablero.Carta.Dinero(4, 1000), decorativo = true)
            ElementoMano(ElementoTablero.Carta.Dinero(1, 100), decorativo = true)
            ElementoMano(ElementoTablero.Carta.Llave(1), decorativo = true)
            ElementoMano(ElementoTablero.Carta.Perseskud(), decorativo = true)
            ElementoMano(ElementoTablero.Carta.Brandy(2), decorativo = true)
            ElementoMano(ElementoTablero.Carta.AcusacionExtra(), decorativo = true)
        }
    }
}

class ElementoMano {
    sealed class Dimensiones(val divisor: Float) {
        object Reducido: Dimensiones(4f)
        class Personalizada(dimensiones: Float): Dimensiones(dimensiones)
        object Mediano: Dimensiones(2f)
        object Grande: Dimensiones(1f)
    }
}