package com.example.composetest.ui.compose.widget

import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AdelaidaTab(posicion: Int, seleccionada: Boolean, texto: String, onClick: (Int) -> Unit) {
    Tab(seleccionada, onClick = { onClick(posicion) }) {
        TabText(texto, seleccionada)
    }
}

@Preview
@Composable
fun AdelaidaTabPreview() {
    AdelaidaTabRow(1) {
        AdelaidaTab(0, false, "Sin seleccionar") {}
        AdelaidaTab(1, true, "Seleccionada") {}
    }
}