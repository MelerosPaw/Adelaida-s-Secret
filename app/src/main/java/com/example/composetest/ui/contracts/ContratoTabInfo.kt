package com.example.composetest.ui.contracts

import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.navegacion.Mensaje

/**
 * @property partida No es un estado que se vaya a cambiar desde aquí, así que es un val.
 */
class EstadoTabInfo(val partida: Partida) {

  sealed class Estado
}

sealed class IntencionTabInfo() {
  class MostrarMensaje(val mensaje: Mensaje): IntencionTabInfo()
  object MostrarDialogoSalir: IntencionTabInfo()
}

fun interface ConsumidorTabInfo {

  fun consumir(vararg intenciones: IntencionTabInfo)

  object Dummy: ConsumidorTabInfo {
    override fun consumir(vararg intenciones: IntencionTabInfo) {
      // Función dummy para previews
    }
  }
}