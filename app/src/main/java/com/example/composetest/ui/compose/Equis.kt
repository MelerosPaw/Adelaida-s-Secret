package com.example.composetest.ui.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.composetest.ui.compose.widget.AdelaidaIconButton

@Composable
fun Equis(onClick: () -> Unit, contentDescription: String, modifier: Modifier = Modifier) {
    AdelaidaIconButton(Icons.Filled.Close, contentDescription, modifier, onClick = onClick)
}