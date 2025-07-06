package com.example.composetest.ui.compose.widget

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.AdelaidaCardDefaults.AdelaidaCardColors

@Composable
fun AdelaidaCard(
    modifier: Modifier = Modifier,
    colors: AdelaidaCardColors = AdelaidaCardDefaults.colors(),
    elevation: CardElevation = AdelaidaCardDefaults.elevation(),
    shape: Shape = AdelaidaCardDefaults.shape(),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val cardColors = CardColors(colors.containerColor, colors.contentColor,
        colors.disabledContainerColor, colors.disabledContentColor)

    onClick?.let {
        Card(
            modifier = modifier,
            colors = cardColors,
            elevation = elevation,
            content = content,
            shape = shape,
            onClick = it,
        )
    } ?: run {
        Card(
            modifier = modifier,
            colors = cardColors,
            elevation = elevation,
            content = content,
            shape = shape,
        )
    }
}

class AdelaidaCardDefaults private constructor() {

    companion object {

        val containerColor: Color
            @Composable get() = Tema.colors.rellenoBoton
        val pressedContainerColor: Color
            @Composable get() = Tema.colors.rellenoBotonPulsado
        val defaultElevation = 4.dp
        val pressedElevation = 18.dp
        val disabledElevation = 2.dp

        @Composable
        fun shape(corners: Dp = 12.dp): Shape = RoundedCornerShape(corners)

        @Composable
        fun colors(
            containerColor: Color = AdelaidaCardDefaults.containerColor,
            pressedContainerColor: Color = AdelaidaCardDefaults.pressedContainerColor,
        ) : AdelaidaCardColors {
            val cardColors = CardDefaults.cardColors()
            return AdelaidaCardColors(
                containerColor,
                pressedContainerColor,
                cardColors.contentColor,
                cardColors.disabledContainerColor,
                cardColors.disabledContentColor
            )
        }

        @Composable
        fun elevation(
            defaultElevation: Dp = AdelaidaCardDefaults.defaultElevation,
            pressedElevation: Dp = AdelaidaCardDefaults.pressedElevation,
            disabledElevation: Dp = AdelaidaCardDefaults.disabledElevation,
        ): CardElevation = CardDefaults.cardElevation(defaultElevation, pressedElevation, disabledElevation)
    }

    class AdelaidaCardColors(
        val containerColor: Color,
        val pressedContainerColor: Color,
        val contentColor: Color,
        val disabledContainerColor: Color,
        val disabledContentColor: Color,

    )
}