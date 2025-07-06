package com.example.composetest.ui.compose.widget

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composetest.ui.compose.theme.Tema

@Composable
fun AdelaidaDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        color = Tema.colors.divisor, thickness = 1.dp,
        modifier = modifier
    )
}