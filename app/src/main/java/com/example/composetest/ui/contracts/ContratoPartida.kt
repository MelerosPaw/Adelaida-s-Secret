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
import com.example.composetest.ui.manager.GestorRonda
import com.example.composetest.ui.viewmodel.MediodiaTardeViewModel.EstadoTablero
import com.example.composetest.ui.viewmodel.PartidaViewModel.InfoRonda

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
  private val _infoRonda: MutableState<Estado.EstadoInfoRonda> = mutableStateOf(Estado.EstadoInfoRonda(null))

  val partida: State<Estado.Partida> = _partida
  val tabActual: State<Estado.TabActual> = _tabActual
  val estadoTablero: State<Estado.Tablero> = _estadoTablero
  val asuntoTurbio: State<Estado.AsuntoTurbioActual> = _asuntoTurbioActual
  val estadoTabInfo: State<Estado.EstadoInfoTab> = _estadoTabInfo
  val infoAccionProhibida: State<Estado.InfoAccionProhibida> = _infoAccionProhibida
  val infoRonda: State<Estado.EstadoInfoRonda> = _infoRonda

  fun set(estado: Estado) {
    when (estado) {
      is Estado.Partida -> _partida.value = estado
      is Estado.TabActual -> _tabActual.value = estado
      is Estado.Tablero -> _estadoTablero.value = estado
      is Estado.AsuntoTurbioActual -> _asuntoTurbioActual.value = estado
      is Estado.EstadoInfoTab -> _estadoTabInfo.value = estado
      is Estado.InfoAccionProhibida -> _infoAccionProhibida.value = estado
      is Estado.EstadoInfoRonda -> _infoRonda.value = estado
    }
  }

  fun setPartida(partida: Partida, haCambiadoDeRonda: Boolean, gestorRonda: GestorRonda) {
    set(Estado.Partida(partida))
    set(Estado.Tablero(partida.tablero?.let { EstadoTablero(it, obtenerElementosFueraDelTablero(partida)) }))
    set(Estado.EstadoInfoTab(partida.fuerzaDefensa))
    initInfoRonda(partida, haCambiadoDeRonda, gestorRonda)

    if (tabActual.value.tab == null || haCambiadoDeRonda) {
      set(Estado.TabActual(TabData.obtenerPorRonda(partida.ronda)))
    }
  }

  private fun obtenerElementosFueraDelTablero(partida: Partida): List<ElementoTablero> =
    partida.jugadores.flatMap { it.cartas() + it.pistas() }

  /**
   * Cuando cambia la partida, solo debemos actualizar la información de la ronda si ha cambiado
   * la ronda. Cualquier otro cambio sobre la ronda se modificará en su sitio.
   */
  private fun initInfoRonda(partida: Partida, haCambiadoDeRonda: Boolean, gestorRonda: GestorRonda) {
    if (haCambiadoDeRonda) {
      with(partida.ronda) {
        set(
          Estado.EstadoInfoRonda(
            InfoRonda(
              ronda = this,
              dia = partida.dia,
              mostrarDialogoSiguienteRonda = false,
              preguntaSiguienteRonda = gestorRonda.getPreguntaSiguienteRonda(this),
              explicacionVisible = false,
              subtitulo = gestorRonda.getSubtitulo(this),
              explicacion = gestorRonda.getExplicacion(this),
              explicacionEsHTMTL = gestorRonda.getExplicacionEsHtml(this),
              mostrarTituloSiguienteRonda = true,
            )
          )
        )
      }
    }
  }

  sealed class Estado {
    class Partida(val partida: PartidaModelo?) : Estado()
    class TabActual(val tab: TabData?) : Estado()
    class Tablero(val estadoTablero: EstadoTablero?) : Estado()
    class AsuntoTurbioActual(val asuntoTurbio: AsuntoTurbio) : Estado()
    class EstadoInfoTab(val fuerzaDefensa: Int?) : Estado()
    class InfoAccionProhibida(val estado: EstadoAccionProhibida): Estado()
    class EstadoInfoRonda(val infoRonda: InfoRonda?): Estado()
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