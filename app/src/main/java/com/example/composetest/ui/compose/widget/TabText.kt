package com.example.composetest.ui.compose.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetest.ui.compose.theme.FondoBoton
import com.example.composetest.ui.compose.theme.TextoNormal

@Composable
fun TabText(text: String, isSelected: Boolean) {
    Text(
        text = text.uppercase(),
        fontWeight = FontWeight.Bold.takeIf { isSelected } ?: FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 10.dp),
    )
}

@Preview
@Composable
fun TabTextPrev() {
    TabRow(0,
        containerColor = FondoBoton,
        contentColor = TextoNormal
    ) {
        TabText(text = "PESTAÑA", true)
        TabText(text = "PESTAÑA 2", false)
    }
}