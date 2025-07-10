package com.example.composetest.ui.contracts

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.AccionProhibida
import com.example.composetest.ui.compose.EstadoAccionProhibida
import com.example.composetest.ui.compose.TabData
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.manager.AsuntoTurbio
import com.example.composetest.ui.viewmodel.MediodiaTardeViewModel.EstadoTablero

typealias PartidaModelo = Partida

class EstadoPartida {

  private val _partida: MutableState<Estado.Partida> = mutableStateOf(Estado.Partida(null))
  private val _tabActual: MutableState<Estado.TabActual> = mutableStateOf(Estado.TabActual(null))
  private val _estadoTablero: MutableState<Estado.Tablero> = mutableStateOf(Estado.Tablero(null))
  private var _asuntoTurbioActual: MutableState<Estado.AsuntoTurbioActual> = mutableStateOf(
    Estado.AsuntoTurbioActual(AsuntoTurbio.Ninguno())
  )
  private val _estadoTabInfo: MutableState<Estado.EstadoInfoTab> = mutableStateOf(Estado.EstadoInfoTab(null))
  private val _infoAccionProhibida: MutableState<Estado.InfoAccionProhibida> = mutableStateOf(
    Estado.InfoAccionProhibida(EstadoAccionProhibida(null, null))
  )

  val partida: State<Estado.Partida> = _partida
  val tabActual: State<Estado.TabActual> = _tabActual
  val estadoTablero: State<Estado.Tablero> = _estadoTablero
  val asuntoTurbio: State<Estado.AsuntoTurbioActual> = _asuntoTurbioActual
  val estadoTabInfo: State<Estado.EstadoInfoTab> = _estadoTabInfo
  val infoAccionProhibida: State<Estado.InfoAccionProhibida> = _infoAccionProhibida

  fun set(estado: Estado) {
    when (estado) {
      is Estado.Partida -> _partida.value = estado
      is Estado.TabActual -> _tabActual.value = estado
      is Estado.Tablero -> _estadoTablero.value = estado
      is Estado.AsuntoTurbioActual -> _asuntoTurbioActual.value = estado
      is Estado.EstadoInfoTab -> _estadoTabInfo.value = estado
      is Estado.InfoAccionProhibida -> _infoAccionProhibida.value = estado
    }
  }

  fun setPartida(partida: PartidaModelo) {
    set(Estado.Partida(partida))
    set(Estado.TabActual(TabData.obtenerPorRonda(partida.ronda)))
    set(Estado.Tablero(partida.tablero?.let { EstadoTablero(it, obtenerElementosFueraDelTablero(partida)) }))
    set(Estado.EstadoInfoTab(partida))
  }

  private fun obtenerElementosFueraDelTablero(partida: Partida): List<ElementoTablero> =
    partida.jugadores.flatMap {
      it.cartas() + it.pistas()
    }

  sealed class Estado {
    class Partida(val partida: PartidaModelo?) : Estado()
    class TabActual(val tab: TabData?) : Estado()
    class Tablero(val estadoTablero: EstadoTablero?) : Estado()
    class AsuntoTurbioActual(val asuntoTurbio: AsuntoTurbio) : Estado()
    class EstadoInfoTab(val partida: PartidaModelo?) : Estado()
    class InfoAccionProhibida(val estado: EstadoAccionProhibida): Estado()
  }
}

sealed class IntencionPartida: MVIIntencion {
  class CambiarTab(val nuevaTab: TabData): IntencionPartida()
  class MostrarMensaje(val mensaje: Mensaje): IntencionPartida()
  class IniciarAsuntoTurbio(val asuntoTurbio: AsuntoTurbio): IntencionPartida()
  class TratarAccionProhibida(val accionProhibida: AccionProhibida): IntencionPartida()
  object MostrarDialogoAbandonar: IntencionPartida()
}

interface ConsumidorPartida: MVIConsumidor<IntencionPartida> {

  class Dummy: ConsumidorPartida {
    override fun consumir(vararg intenciones: IntencionPartida) { /* no-op. Used in previews */ }
  }
}