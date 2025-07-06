package com.example.composetest.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.composetest.ui.compose.widget.AdelaidaIconButton

@Composable
fun CustomCheckbox(
    checked: Boolean,
    icons: CustomIcon,
    modifier: Modifier = Modifier,
    onCheckedChanged: (isChecked: Boolean) -> Unit,
) {
    val icon = icons.checkedIcon.takeIf { checked } ?: icons.uncheckedIcon
    AdelaidaIconButton(icon.drawable, icon.contentDescription, modifier) { onCheckedChanged(!checked) }
}

class CheckboxIcon(val drawable: ImageVector, val contentDescription: String?)

class CustomIcon(val uncheckedIcon: CheckboxIcon, val checkedIcon: CheckboxIcon)