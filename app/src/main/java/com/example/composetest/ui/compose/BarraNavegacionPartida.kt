package com.example.composetest.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssistantPhoto
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetest.R
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.screen.PreviewComponente
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.AdelaidaIcon
import com.example.composetest.ui.compose.widget.AdelaidaText

@Composable
fun BarraNavegacionPartida(tabSeleccionada: TabData?, tabs: Array<TabWrapper>) {
  if (tabSeleccionada != null) {
    NavigationBar(
      containerColor = Tema.colors.toolbarContainer,
      // Esto es necesario porque aquí no hay que aplicar insets. El componente está pensado para ser
      // utilizado en la pantalla base de la aplicación, pero allí ya se le están aplicando los insets
      // al contenedor del grafo, por lo que no hay que aplicarlos aquí nuevamente.
      windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
      tabs.forEach { tab ->
        val isSelected = tabSeleccionada == tab.data

        NavigationBarItem(
          selected = isSelected,
          onClick = { tab.alClicar(tab.data) },
          icon = { tab.icono(isSelected) },
          label = { AdelaidaText(tab.data.nombre, fontSize = 14.sp, color = Color.White) },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = Tema.colors.indicadorSeleccionBarraNavegacion,
          )
        )
      }
    }
  }
}

@Composable
fun getColorIconoNavegacion(seleccionado: Boolean) : Color = with(Tema.colors) {
  iconoBarraNavegacionSelecionado.takeIf { seleccionado } ?: iconoBarraNavegacion
}

@Composable
private fun IconoTab(tabData: TabData, seleccionado: Boolean) {
  val modifier = Modifier.size(24.dp)
  val tint = getColorIconoNavegacion(seleccionado)

  when (tabData.toDraw) {
    is ToDraw.Id -> AdelaidaIcon(painterResource(tabData.toDraw.id), null, modifier, tint)
    is ToDraw.Vector -> AdelaidaIcon(imageVector = tabData.toDraw.imageVector, null, modifier, tint)
  }
}

@Preview
@Composable
private fun Preview() {
  PreviewComponente {
    BarraNavegacionPartida(TabData.INFO, getTabs {  })
  }
}

fun getTabs(cambiarPaginaActual: (TabData) -> Unit): Array<TabWrapper> =
  TabData.entries.map {
    TabWrapper(
      data = it,
      icono = { seleccionado -> IconoTab(it, seleccionado) },
      alClicar = cambiarPaginaActual
    )
  }.toTypedArray()

enum class TabData(val nombre: String, val posicion: Int, val toDraw: ToDraw) {
  TABLERO("TABLERO", 0, ToDraw.Id(R.drawable.ic_tablero)),
  JUGADORES("JUGADORES", 1, ToDraw.Vector(Icons.Default.People)),
  EVENTOS("EVENTOS", 2, ToDraw.Vector(Icons.Default.AssistantPhoto)),
  INFO("INFO", 3, ToDraw.Vector(Icons.Default.Info));

  companion object {
    fun obtenerPorPosicion(posicion: Int) : TabData = entries.firstOrNull { it.posicion == posicion } ?: TABLERO

    fun obtenerPorRonda(ronda: Partida.Ronda) : TabData = when(ronda) {
      Partida.Ronda.MANANA -> TABLERO
      Partida.Ronda.MEDIODIA -> TABLERO
      Partida.Ronda.TARDE -> JUGADORES
      Partida.Ronda.NOCHE -> EVENTOS
      Partida.Ronda.NO_VALIDA -> INFO
    }
  }
}

sealed class ToDraw() {
  class Vector(val imageVector: ImageVector): ToDraw()
  class Id(@DrawableRes val id: Int): ToDraw()
}

class TabWrapper(
  val data: TabData,
  val icono: @Composable (isSelected: Boolean) -> Unit,
  val alClicar: (paginaClicada: TabData) -> Unit
)