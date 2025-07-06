package com.example.composetest.ui.compose.widget

import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetest.ui.compose.theme.FondoBoton
import com.example.composetest.ui.compose.theme.TextoNormal

@Composable
fun AdelaidaTabRow(
    currentPage: Int,
    colorContenedor: Color = FondoBoton,
    colorContenido: Color = TextoNormal,
    tabs: @Composable () -> Unit
) {
    TabRow(
        currentPage,
        containerColor = colorContenedor,
        contentColor = colorContenido,
        // Esto espera que pintes el indicador
        indicator = { tabPositions ->
            // Solo pinta si la página seleccionada existe
            if (currentPage < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    color = Color.White,
                    modifier = Modifier.tabIndicatorOffset(tabPositions[currentPage])
                )
            }
        },
        tabs = tabs,
    )
}

@Preview
@Composable
fun AdelaidaTabRowPreview() {
    AdelaidaTabRow(1) {
        AdelaidaTab(0, false, "Sin seleccionar") {}
        AdelaidaTab(1, true, "Seleccionada") {}
    }
}