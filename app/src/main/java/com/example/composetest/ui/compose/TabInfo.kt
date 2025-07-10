package com.example.composetest.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.modifiers.onLongClick
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.sampledata.partidas
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.widget.AdelaidaCard
import com.example.composetest.ui.compose.widget.AdelaidaCardDefaults
import com.example.composetest.ui.compose.widget.AdelaidaIcon
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.compose.widget.AdelaidaTextDefaults
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.contracts.ConsumidorTabInfo
import com.example.composetest.ui.contracts.EstadoPartida
import com.example.composetest.ui.contracts.IntencionTabInfo

@Composable
fun TabInfo(estado: EstadoPartida.Estado.EstadoInfoTab, consumidor: ConsumidorTabInfo) {
    Column {
      FuerzaDefensa(estado, consumidor)
      Abandonar(consumidor)
    }
}

@Composable
private fun FuerzaDefensa(estado: EstadoPartida.Estado.EstadoInfoTab, consumidor: ConsumidorTabInfo) {
  Row(
    Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Titulo("Fuerza de la defensa", nivel = NivelTitulo.Nivel3, modifier = Modifier.weight(1f))
    AdelaidaCard(
      shape = AdelaidaCardDefaults.shape(4.dp),
      colors = AdelaidaCardDefaults.colors(containerColor = Color.White),
    ) {
      Box(
        Modifier
          .onLongClick { consumidor.consumir(IntencionTabInfo.MostrarMensaje(Mensaje("Pulsado"))) }
          .padding(vertical = 10.dp, horizontal = 12.dp)
      ) {
        AdelaidaText(
          text = estado.fuerzaDefensa?.toString() ?: "?",
          fontWeight = FontWeight.Black,
          color = Color(0xFF0C2684),
        )
      }
    }
  }
}

@Composable
private fun Abandonar(consumidorTabInfo: ConsumidorTabInfo) {
  Row(
    Modifier
      .clickable(
        onClickLabel = "salir de la partida",
        onClick = { consumidorTabInfo.consumir(IntencionTabInfo.MostrarDialogoSalir) }
      )
      .padding(start = 16.dp)
      .semantics(true) { role = Role.Button }
  ) {
    AdelaidaIcon(
      imageVector = Icons.AutoMirrored.Default.ExitToApp,
      contentDescription = null,
      tint = AdelaidaTextDefaults.color
    )
    AdelaidaText("Abandonar partida", Modifier.padding(start = 16.dp))
  }
}

@NightAndDay
@Composable
private fun PreviewTabInfo() {
  val partida: Partida = partidas(1)[0].copy(fuerzaDefensa = 12)

  ScreenPreviewMarron {
    TabInfo(
      estado = EstadoPartida.Estado.EstadoInfoTab(partida.fuerzaDefensa),
      ConsumidorTabInfo.Dummy
    )
  }
}