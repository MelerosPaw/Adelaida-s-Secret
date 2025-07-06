package com.example.composetest.ui.compose.widget

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetest.ui.compose.screen.PreviewFondo
import com.example.composetest.ui.compose.screen.PreviewFondoVerde
import com.example.composetest.ui.compose.theme.Tema

@Composable
fun AdelaidaOutlinedTextField(
    texto: String,
    label: String,
    modifier: Modifier = Modifier,
    onDoneClicked: KeyboardActionScope.(String) -> Unit = {},
    onTextChanged: (String) -> Unit,
    error: String = "",
) {

    var mensajeError by remember { mutableStateOf(error) }

    Column {
        OutlinedTextField(
            texto, {
                mensajeError = ""
                onTextChanged(it)
            },
            label = { AdelaidaText(label, Modifier.background(color = Color.Transparent), color = Color.Unspecified) },
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                errorContainerColor = Color.Transparent,
                errorTextColor = Tema.colors.errorCampoTexto,
                errorLabelColor = Tema.colors.errorCampoTexto,
                unfocusedContainerColor = Color.Transparent,
                unfocusedLabelColor = Tema.colors.contenidoDialogos,
                focusedLabelColor = Tema.colors.contenidoDialogos,
                unfocusedTextColor = Tema.colors.contenidoDialogos,
                focusedTextColor = Tema.colors.contenidoDialogos,
                focusedContainerColor = Color.Transparent,
                cursorColor = Tema.colors.contenidoDialogos,
                errorCursorColor = Tema.colors.errorCampoTexto,
                focusedIndicatorColor = Tema.colors.contenidoDialogos,
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDoneClicked(texto) }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(KeyboardCapitalization.Sentences),
            isError = error.isNotBlank(),
            textStyle = TextStyle.Default.copy(
                fontSize = 16.sp,
                fontFamily = AdelaidaTextDefaults.font,
                fontWeight = AdelaidaTextDefaults.fontWeight,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier),
        )

        if (error.isNotBlank()) {
            Text(
                error,
                color = Tema.colors.errorCampoTexto,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewOutlinedTextField() {
    Column {
        PreviewFondo {
            LaPreview()
        }

        PreviewFondoVerde {
            LaPreview()
        }
    }
}

@Composable
private fun LaPreview() {
    Column {
        AdelaidaOutlinedTextField(
            "Tu texto escrito",
            "Con error",
            onTextChanged = { },
            error = "El error"
        )

        AdelaidaOutlinedTextField(
            "Tu texto escrito",
            "Sin error",
            onTextChanged = { },
        )
    }
}