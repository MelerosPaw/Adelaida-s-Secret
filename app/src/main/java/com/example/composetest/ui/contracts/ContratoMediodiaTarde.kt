package com.example.composetest.ui.contracts

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.composetest.model.Partida

class EstadoMediodiaTarde(val partida: Partida) {

  private val _estadoTabInfo: MutableState<Estado.TabInfo> = mutableStateOf(Estado.TabInfo(EstadoTabInfo(partida)))
  val estadoTabInfo: State<Estado.TabInfo> = _estadoTabInfo

  sealed class Estado {
    class TabInfo(val tabInfo: EstadoTabInfo) : Estado()
  }
}