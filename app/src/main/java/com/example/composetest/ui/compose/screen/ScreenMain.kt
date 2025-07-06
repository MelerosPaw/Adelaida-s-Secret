package com.example.composetest.ui.compose.screen

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetest.R
import com.example.composetest.ui.compose.navegacion.NavegadorCreacion
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.widget.AdelaidaButton
import com.example.composetest.ui.compose.widget.AdelaidaText

@Composable
fun ScreenMain(
  onConfiguracionToolbarCambiada: (NavegadorCreacion.ConfiguracionToolbar) -> Unit,
  onNavegarATableroDebug: () -> Unit,
  onNavegarANuevaPartida: () -> Unit,
  onNavegarACargarPartida: () -> Unit,
) {
  ScreenMain(onNavegarATableroDebug, onNavegarANuevaPartida, onNavegarACargarPartida)
}

@Composable
private fun ScreenMain(
  onNavegarATableroDebug: () -> Unit,
  onNavegarANuevaPartida: () -> Unit,
  onNavegarACargarPartida: () -> Unit,
) {
  ScreenVerde(
    configuracionToolbar = NavegadorCreacion.ConfiguracionToolbar(
      titulo = NavegadorCreacion.ConfiguracionToolbar.titulo("Los Secretos de Adelaida"),
    )
  ) {
    Image(
      painterResource(R.drawable.fondo),
      null,
      Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
      contentScale = ContentScale.Crop
    )

    Column(Modifier.padding(MargenEstandar)) {
      IrATablero(onNavegarATableroDebug)
      NuevaPartida(onNavegarANuevaPartida)
      CargarPartida(onNavegarACargarPartida)
      BotonAnimado()
    }
  }
}

@Composable
private fun IrATablero(onNavegarATableroDebug: () -> Unit) {
  AdelaidaButton(
    onClick = { onNavegarATableroDebug() },
    modifier = Modifier.fillMaxWidth(),
  ) {
    Text(text = "Ir al tablero")
  }
}

@Composable
private fun NuevaPartida(onNavegarANuevaPartida: () -> Unit) {
  AdelaidaButton(
    onClick = { onNavegarANuevaPartida() },
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 8.dp),
  ) {
    Text(text = "Nuevo juego")
  }
}

@Composable
private fun CargarPartida(onNavegarACargarPartida: () -> Unit) {
  AdelaidaButton(
    onClick = { onNavegarACargarPartida() },
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 8.dp),
  ) {
    Text(text = "Cargar partida")
  }
}

@Composable
private fun BotonAnimado() {
  var posicion by remember { mutableStateOf(Posicion.IZQUIERDA) }
  val accionCambio = {
    posicion = when (posicion) {
      Posicion.IZQUIERDA -> Posicion.CENTRO
      Posicion.CENTRO -> Posicion.DERECHA
      Posicion.DERECHA -> Posicion.IZQUIERDA
    }
  }

  AdelaidaButton(accionCambio, Modifier.padding(top = 8.dp)) {
    Text("Cambiar posición")
  }

  AnimatedContent(
    posicion, label = "Algo",
    transitionSpec = {
      when {
        Posicion.IZQUIERDA isTransitioningTo Posicion.CENTRO ->
          slideInVertically() togetherWith slideOutVertically()

        Posicion.CENTRO isTransitioningTo Posicion.DERECHA ->
          slideInVertically(
            animationSpec = tween(2000),
            initialOffsetY = { fullHeight -> fullHeight }
          ) togetherWith
                  slideOutVertically(
                    animationSpec = tween(2000),
                    targetOffsetY = { fullHeight -> fullHeight }
                  )

        else -> slideInHorizontally() togetherWith slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
      }
    }
  ) { posicion ->
    val align = when (posicion) {
      Posicion.IZQUIERDA -> TextAlign.Start
      Posicion.CENTRO -> TextAlign.Center
      Posicion.DERECHA -> TextAlign.End
    }
    val alignment = when (posicion) {
      Posicion.IZQUIERDA -> Alignment.Start
      Posicion.CENTRO -> Alignment.CenterHorizontally
      Posicion.DERECHA -> Alignment.End
    }
    Column {
      Image(painterResource(R.drawable.ic_habito_grande), "Cosas que cambian", Modifier.align(alignment))
      AdelaidaText("Texto que se mueve", Modifier.fillMaxWidth(), textAlign = align)
      Text("Texto que sale como un ticker", Modifier
        .width(100.dp)
        .basicMarquee(), textAlign = align)
    }
  }
}

@Preview(name = "Claro", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMain() {
  ScreenPreviewMarron {
    ScreenMain({}, {}, {})
  }
}

enum class Posicion {
  IZQUIERDA, CENTRO, DERECHA
}