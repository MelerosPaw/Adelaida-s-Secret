package com.example.composetest.ui.compose.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.composetest.ui.compose.theme.Tema

@Composable
fun TextCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    colors: TextCheckboxColors = TextCheckboxDefaults.colors()
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(enabled) { onCheckedChange(!checked) }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                checkedColor = colors.checkedColor,
                checkmarkColor = colors.checkmarkColor,
                uncheckedColor = colors.uncheckedColor,
            )
        )

        AdelaidaText(text, color = colors.contentColor.takeIf { enabled } ?: colors.disabledColor)
    }
}

data class TextCheckboxColors(
    val contentColor: Color,
    val disabledColor: Color,
    val checkedColor: Color,
    val checkmarkColor: Color,
    val uncheckedColor: Color,
)

class TextCheckboxDefaults {

    companion object {

        val contentColor: Color
            @Composable get() = Tema.colors.texto
        val disabledColor: Color
            @Composable get() = Tema.colors.textoBotonInhabilitado
        val checkedColor: Color
            @Composable get() = Tema.colors.fondoCheckboxMarcado
        val checkmarkColor: Color
            @Composable get() = Tema.colors.marcaCheckbox
        val uncheckedColor: Color
            @Composable get() = Tema.colors.fondoCheckboxDesmarcado

        @Composable
        fun colors(
            contentColor: Color = TextCheckboxDefaults.contentColor,
            disabledColor: Color = TextCheckboxDefaults.disabledColor,
            checkedColor: Color = TextCheckboxDefaults.checkedColor,
            checkmarkColor: Color = TextCheckboxDefaults.checkmarkColor,
            uncheckedColor: Color = TextCheckboxDefaults.uncheckedColor,
        ): TextCheckboxColors = TextCheckboxColors(
            contentColor,
            disabledColor,
            checkedColor,
            checkmarkColor,
            uncheckedColor
        )
    }
}