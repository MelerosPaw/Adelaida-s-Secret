package com.example.composetest.ui.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import com.example.composetest.R
import com.example.composetest.ui.viewmodel.TableroViewModel

@Composable
fun EsquemaTablero(viewModel: TableroViewModel) {

    val impresora by remember { viewModel.impresoraState }
    val mostrarEsquema by remember { viewModel.mostrarEsquemaState }

    if (mostrarEsquema) {
        impresora?.let {
            Text(
                text = it.esquema().toString(),
                fontSize = 10.sp,
                fontFamily = FontFamily(Font(R.font.jetbrainsregular)),
                lineHeight = TextUnit(10f, TextUnitType.Sp)
            )
        }
    }
}

