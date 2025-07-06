package com.example.composetest.ui.compose.widget

import androidx.compose.runtime.Composable
import com.example.composetest.ui.compose.theme.Tema

@Composable
fun DialogTextCheckbox(
  text: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  enabled: Boolean = true,
  colors: TextCheckboxColors = TextCheckboxDefaults.colors(
    contentColor = Tema.colors.contenidoDialogos,
    disabledColor = Tema.colors.contenidoDialogosInhabilitado,
    checkedColor = Tema.colors.contenidoDialogos,
    uncheckedColor = Tema.colors.contenidoDialogos,
    checkmarkColor = Tema.colors.fondoDialogos
  )
) {
  TextCheckbox(text, checked, onCheckedChange, enabled, colors)
}

