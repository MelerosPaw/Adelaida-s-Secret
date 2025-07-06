package com.example.composetest.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.composetest.ui.compose.theme.FondoPantallaCargando

@Composable
fun Cargando(mostrar: Boolean = true) {
  if (mostrar) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.background(FondoPantallaCargando).clickable(false) { }
    ) {

      CircularProgressIndicator(color = Color.White)
    }
  }
}