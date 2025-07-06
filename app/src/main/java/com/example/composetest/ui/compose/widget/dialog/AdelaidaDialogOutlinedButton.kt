package com.example.composetest.ui.compose.widget.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.example.composetest.ui.compose.theme.Tema

@Composable
fun AdelaidaDialogOutlinedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: AdelaidaDialogOutlinedButtonColors = AdelaidaDialogOutlinedButtonDefaults.colors(enabled),
  contentPadding: PaddingValues = AdelaidaDialogOutlinedButtonDefaults.contentPadding(),
  content: @Composable RowScope.() -> Unit,
) {
  val border = BorderStroke(1.dp, colors.borderColor)
  val layoutDirection = LocalLayoutDirection.current

  OutlinedButton(onClick, modifier, enabled,
    contentPadding = PaddingValues(top = 0.dp, bottom = 0.dp,
      start = contentPadding.calculateStartPadding(layoutDirection),
      end = contentPadding.calculateEndPadding(layoutDirection)
    ),
    border = border, colors = colors.buttonColors, content = content)
}

class AdelaidaDialogOutlinedButtonDefaults {

  companion object {
    val buttonColors: ButtonColors
      @Composable get() = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Transparent)

    @Composable
    fun borderColor(isEnabled: Boolean): Color =
      Tema.colors.contenidoDialogos.takeIf { isEnabled } ?: Tema.colors.contenidoDialogosInhabilitado

    @Composable
    fun colors(isEnabled: Boolean): AdelaidaDialogOutlinedButtonColors = AdelaidaDialogOutlinedButtonColors(
      buttonColors = buttonColors,
      borderColor = borderColor(isEnabled)
    )

    @Composable
    fun contentPadding(): PaddingValues = ButtonDefaults.ContentPadding
  }
}

class AdelaidaDialogOutlinedButtonColors(
  val buttonColors: ButtonColors,
  val borderColor: Color,
)