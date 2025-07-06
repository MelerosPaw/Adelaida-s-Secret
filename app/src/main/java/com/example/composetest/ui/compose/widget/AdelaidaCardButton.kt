package com.example.composetest.ui.compose.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composetest.ui.compose.modifiers.cuadrado

@Composable
fun AdelaidaCardButton(
    estaPulsado: Boolean,
    modifier: Modifier = Modifier,
    cuadrado: Boolean = true,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    AdelaidaCard(
        elevation = CardDefaults.cardElevation(0.dp).takeIf { estaPulsado } ?: AdelaidaCardDefaults.elevation(),
        colors = AdelaidaCardDefaults.colors(AdelaidaCardDefaults.pressedContainerColor.takeIf { estaPulsado } ?: AdelaidaCardDefaults.containerColor),
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            Modifier.cuadrado().takeIf { cuadrado } ?: Modifier,
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}