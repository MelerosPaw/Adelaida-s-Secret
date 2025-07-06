package com.example.composetest.ui.compose.widget

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetest.ui.compose.screen.PreviewFondo
import com.example.composetest.ui.compose.screen.PreviewFondoVerde
import com.example.composetest.ui.compose.theme.Tema

@Composable
fun AdelaidaTextField(
    texto: String,
    onTextChanged: (String) -> Unit,
    label: String,
    error: String?,
    onDoneClicked: KeyboardActionScope.(String) -> Unit = {},
) {
    Column {
        TextField(
            texto, onTextChanged,
            label = { Text(label) },
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Tema.colors.fondoCampoTexto,
                focusedContainerColor = Tema.colors.fondoCampoTextoSeleccionado,
                errorContainerColor = Tema.colors.fondoCampoTextoSeleccionado,
                focusedLabelColor = Tema.colors.textoCuadroTexto,
                unfocusedLabelColor = Color(0xFF3C3C3C),
                focusedTextColor = Tema.colors.textoCuadroTexto,
                unfocusedTextColor = Tema.colors.textoCuadroTexto,
                errorLabelColor = Tema.colors.errorCampoTexto,
                errorTextColor = Tema.colors.errorCampoTexto,
                cursorColor = Tema.colors.textoCuadroTexto,
                errorCursorColor = Tema.colors.texto,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            keyboardActions = KeyboardActions(onDone = {
                texto.takeIf { error.isNullOrBlank() }?.let { onDoneClicked(it) }

            }),
            isError = error?.isNotBlank() == true,
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(KeyboardCapitalization.Sentences)
        )

        error.takeIf { !it.isNullOrBlank() }?.let {
            Text(
                it,
                color = Tema.colors.errorCampoTexto,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun P1() {
    Column {
        PreviewFondo {
            Column {
                AdelaidaTextField("Texto marrón", {}, "Sin error", error = null)
                AdelaidaTextField("Texto marrón", {}, "Con error", error = "El error")
            }
        }

        PreviewFondoVerde {
            Column {
                AdelaidaTextField("Texto verde", {}, "Sin error", error = null)
                AdelaidaTextField("Texto verde", {}, "Con error", error = "El error")
            }
        }
    }
}