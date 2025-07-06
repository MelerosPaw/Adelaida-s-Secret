package com.example.composetest.ui.compose.widget

import android.content.res.Configuration
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetest.R
import com.example.composetest.ui.compose.modifiers.ninePatchBackground
import com.example.composetest.ui.compose.screen.PreviewFondo
import com.example.composetest.ui.compose.screen.PreviewFondoVerde
import com.example.composetest.ui.compose.theme.Tema

@Composable
fun AdelaidaButton(
  onClick: () -> Unit,
  text: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: ButtonColors = AdelaidaButtonDefaults.colors(),
) {
  AdelaidaButton(onClick, modifier, enabled, colors) {
    AdelaidaText(text)
  }
}

@Composable
fun AdelaidaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = AdelaidaButtonDefaults.colors(),
    content: @Composable RowScope.() -> Unit
) {
    Box {
        Button(
            onClick,
            enabled = enabled,
            modifier = Modifier.indication(remember { MutableInteractionSource() }, null).then(modifier)
              .semantics {
                onClick(label = "que haga algo", action = null)
              },
            colors = colors.copy(containerColor = colors.containerColor.copy(alpha = 0.8f)),
            shape = RectangleShape,
            content = content,
        )
      val colorDecorado = Tema.colors.decoradoBoton.toArgb()
      val buttonPadding = ButtonDefaults.ContentPadding
      Box(
        modifier
          .matchParentSize()
          .padding(
            top = buttonPadding.calculateTopPadding() - 2.dp,
            bottom = buttonPadding.calculateBottomPadding() - 1.dp,
            start = 1.dp,
            end = 2.dp
          ).ninePatchBackground(R.drawable.contorno_botones, colorDecorado)
      )
    }
}

class AdelaidaButtonDefaults {

    companion object {

        val contentColor: Color
            @Composable get() = Tema.colors.contenidoBoton
        val containerColor: Color
            @Composable get() = Tema.colors.rellenoBoton
        val textoBotonInhabilitado: Color
            @Composable get() = Tema.colors.textoBotonInhabilitado

        @Composable
        fun colors(
            contentColor: Color = Companion.contentColor,
            containerColor: Color = Companion.containerColor,
            disabledContentColor: Color = textoBotonInhabilitado
        ): ButtonColors = ButtonDefaults.buttonColors(
            contentColor = contentColor,
            containerColor = containerColor,
            disabledContentColor = disabledContentColor
        )
    }
}

@Composable
@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun Boton() {
    Column {
        PreviewFondo {
            AdelaidaButton({}) { AdelaidaText("Botón marrón") }
        }

        PreviewFondoVerde {
            AdelaidaButton({}) { AdelaidaText("Botón verdaderamente verde") }
        }
    }
}