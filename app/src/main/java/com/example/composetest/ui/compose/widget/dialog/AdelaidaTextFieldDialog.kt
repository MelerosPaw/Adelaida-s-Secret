package com.example.composetest.ui.compose.widget.dialog

import BotonDialogo
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.AdelaidaOutlinedTextField

@Composable
fun AdelaidaTextFieldDialog(
    mensaje: String,
    label: String,
    textoCampo: String,
    onTextoCampoChanged: (String) -> Unit = {},
    textoSi: String,
    onClickSi: (String) -> Unit,
    textoNo: String,
    onClickNo: () -> Unit,
    error: String = "",
    onDismiss: () -> Unit = { /* No se puede cerrar sin realizar una acción. */ },
) {
    AdelaidaDialog(onDismiss) {
        Text(
            mensaje, color = Tema.colors.contenidoDialogos, fontWeight = FontWeight.Bold, fontSize = 16.sp,
            modifier = Modifier.padding(bottom = MargenEstandar)
        )

        var texto by remember { mutableStateOf(textoCampo) }
        val focusManager = LocalFocusManager.current

        AdelaidaOutlinedTextField(
            texto, label, error = error,
            onTextChanged = {
                texto = it
                onTextoCampoChanged(it)
            },
            onDoneClicked = { focusManager.clearFocus() },
        )

        BotonDialogo(textoSi, onClick = { onClickSi(texto) })
        BotonDialogo(textoNo, onClick = onClickNo)
    }
}


@Preview
@Composable
fun PreviewTextFieldDialog() {
    ScreenPreviewMarron {
        AdelaidaTextFieldDialog(
            "¿Quieres meter aquí, niño?",
            "Lo metes aquí",
            "Estoy rellenando, ¿ves?", {},
            "Eso mismo me vale", {},
            "Que mira, que paso", {},
            "¡Terrible error!")
    }
}