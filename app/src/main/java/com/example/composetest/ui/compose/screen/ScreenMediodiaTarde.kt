package com.example.composetest.ui.compose.screen

import TabJugadores
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.composetest.model.Jugador
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.AccionProhibida
import com.example.composetest.ui.compose.BarraNavegacionPartida
import com.example.composetest.ui.compose.DialogoAccionProhibida
import com.example.composetest.ui.compose.EstadoAccionProhibida
import com.example.composetest.ui.compose.NightAndDay
import com.example.composetest.ui.compose.PosibleAccionProhibida
import com.example.composetest.ui.compose.TabData
import com.example.composetest.ui.compose.TabTableroPartida
import com.example.composetest.ui.compose.TabWrapper
import com.example.composetest.ui.compose.navegacion.EventoBooleano
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.sampledata.jugadores
import com.example.composetest.ui.compose.sampledata.partidas
import com.example.composetest.ui.compose.theme.AdelaidaTheme
import com.example.composetest.ui.compose.theme.AdelaidaThemeVerde
import com.example.composetest.ui.compose.widget.AdelaidaTab
import com.example.composetest.ui.compose.widget.AdelaidaTabRow
import com.example.composetest.ui.contracts.ConsumidorTabInfo
import com.example.composetest.ui.contracts.EstadoMediodiaTarde
import com.example.composetest.ui.manager.AsuntoTurbio
import com.example.composetest.ui.manager.AsuntoTurbio.Ninguno
import com.example.composetest.ui.manager.GestorRondaMediodia
import com.example.composetest.ui.viewmodel.MediodiaTardeViewModel
import com.example.composetest.ui.viewmodel.MediodiaTardeViewModel.EstadoTablero
import com.example.composetest.ui.viewmodel.ResourceManager
import com.example.composetest.ui.viewmodel.TabJugadoresViewModel

@Composable
fun ScreenMediodiaTarde(
  partida: Partida,
  onMensaje: (Mensaje) -> Unit,
  filtrosAbiertos: State<Boolean>,
  onCerrarFiltros: () -> Unit,
  onMostrarPapelera: (Boolean) -> Unit,
  cambioRondaSolicitado: Boolean,
  onCondicionesCambioRondaSatisfechas: (Boolean) -> Unit,
  cambiarATarde: State<EventoBooleano>?,
  onMostrarMensajeAbandondar: () -> Unit,
) {
  val context = LocalContext.current
  val viewModel: MediodiaTardeViewModel = hiltViewModel()
  viewModel.onMensaje = onMensaje
  viewModel.inicializar(partida, onMostrarPapelera, cambioRondaSolicitado,
    onCondicionesCambioRondaSatisfechas, onMostrarMensajeAbandondar, context)

  cambiarATarde?.let {
    val cambiarATarde by remember { it }

    if (cambiarATarde.consume() == true) {
      viewModel.cambiarATarde(partida)
    }
  }

  viewModel.estados?.let {
    ScreenMediodiaTarde(
      partida,
      viewModel.tabs,
      viewModel.paginaActual,
      viewModel.estadoTablero,
      partida.jugadores.toList(),
      viewModel::iniciarAsuntoTurbio,
      viewModel.asuntoTurbio,
      viewModel.sePuedeComprar,
      filtrosAbiertos,
      onCerrarFiltros,
      viewModel.estadoAccionProhibida,
      viewModel::onAccionProhibida,
      cambioRondaSolicitado,
      onCondicionesCambioRondaSatisfechas,
      it,
      viewModel.consumidorTabInfo
    )
  }
}

@Composable
private fun ScreenMediodiaTarde(
  partida: Partida,
  tabs: Array<TabWrapper>,
  paginaActual: State<TabData>,
  estadoTablero: State<EstadoTablero?>,
  jugadores: List<Jugador>?,
  iniciarAsuntoTurbio: (AsuntoTurbio) -> Unit,
  asuntoTurbio: State<AsuntoTurbio>,
  sePuedeComprar: State<Boolean>,
  filtrosAbiertos: State<Boolean>,
  cerrarFiltros: () -> Unit,
  accionProhibida: State<EstadoAccionProhibida?>,
  onAccionProhibida: (AccionProhibida) -> Unit,
  cambioRondaSolicitado: Boolean,
  onCondicionesCambioRondaSatisfechas: (Boolean) -> Unit,
  estados: EstadoMediodiaTarde,
  consumidorTabInfo: ConsumidorTabInfo
) {
  val accionProhibida by remember { accionProhibida }
  val paginaActual by remember { paginaActual }
  val asuntoTurbio by remember { asuntoTurbio }
  val estadoTablero by remember { estadoTablero }

  Column(Modifier.fillMaxSize()) {
//  Tabs(paginaActual, asuntoTurbio is Ninguno, tabs)
    Contenido(paginaActual, asuntoTurbio, filtrosAbiertos, estadoTablero,
      cerrarFiltros, jugadores, sePuedeComprar, iniciarAsuntoTurbio, onAccionProhibida,
      estados, consumidorTabInfo, onCondicionesCambioRondaSatisfechas)
    BarraNavegacionPartida(paginaActual, tabs)
    DialogoAccionProhibida(accionProhibida)
  }
}

@Composable
private fun ColumnScope.Contenido(
  tabActual: TabData,
  asuntoTurbio: AsuntoTurbio,
  filtrosAbiertos: State<Boolean>,
  estadoTablero: EstadoTablero?,
  cerrarFiltros: () -> Unit,
  jugadores: List<Jugador>?,
  sePuedeComprar: State<Boolean>,
  iniciarAsuntoTurbio: (AsuntoTurbio) -> Unit,
  onAccionProhibida: (AccionProhibida) -> Unit,
  estados: EstadoMediodiaTarde,
  consumidorTabInfo: ConsumidorTabInfo,
  onCondicionesCambioRondaSatisfechas: (Boolean) -> Unit,
) {
  val sePuedeComprar by remember { sePuedeComprar }

  Box(Modifier.weight(1f)) {
    when (tabActual) {
      TabData.TABLERO -> TabTableroPartida(filtrosAbiertos, estadoTablero,
        jugadores, estados.partida.id, cerrarFiltros, onAccionProhibida)
      TabData.JUGADORES -> TabJugadores(asuntoTurbio, estados.partida.id,
        jugadores, sePuedeComprar, iniciarAsuntoTurbio, onAccionProhibida)
      TabData.EVENTOS -> ScreenNoche(estados.partida, false, onCondicionesCambioRondaSatisfechas, {})
      TabData.INFO -> {
        val tabInfo by remember { estados.estadoTabInfo }
//        TabInfo(tabInfo.tabInfo, consumidorTabInfo)
      }
    }
  }
}

@Composable
private fun Tabs(
  paginaActual: Int,
  mostrar: Boolean,
  tabs: Array<TabWrapper>,
) {
  if (mostrar) {
    AdelaidaTabRow(paginaActual) {
      tabs.forEach {
        AdelaidaTab(it.data.posicion, it.data.posicion == paginaActual, it.data.nombre, { posicion -> it.alClicar(
          TabData.obtenerPorPosicion(posicion)) })
      }
    }
  }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun ScreenPartidaPreview() {
  ScreenPreviewMarron {
    val jugadores = jugadores(
      "Pablo, con un apellidaco que te cagas",
      "Su madre",
      "Algún vagabundo que no tenía qué comer y que quería jugar con nosotros"
    )

    val asuntoTurbio = AsuntoTurbio.Robo(
      TabJugadoresViewModel.MostrarElementos.Vitrina(jugadores()[0])
    )
    val gestorRonda = GestorRondaMediodia { }
    val accionProhibida = AccionProhibida(PosibleAccionProhibida.Robo(), {}, {})
    val advertenciaRes =
      gestorRonda.advertenciaAccionProhibida(accionProhibida.posibleAccionProhibida)
    val advertencia = advertenciaRes?.let { ResourceManager(LocalContext.current).getString(it) }
    val estadoAccionProhibida = advertencia?.let {
      EstadoAccionProhibida(it, accionProhibida)
    }
    val partida = partidas(1)[0]

    ScreenMediodiaTarde(
      partida = partida,
      tabs = MediodiaTardeViewModel(SavedStateHandle()).tabs,
      paginaActual = mutableStateOf(TabData.JUGADORES),
      estadoTablero = mutableStateOf(null),
      jugadores,
      {},
      asuntoTurbio = mutableStateOf(Ninguno()),
      sePuedeComprar = mutableStateOf(false),
      filtrosAbiertos = mutableStateOf(true),
      {},
      accionProhibida = mutableStateOf(null),
      {},
      false,
      {},
      EstadoMediodiaTarde(partida),
      ConsumidorTabInfo.Dummy
    )
  }
}

@Composable
@NightAndDay
private fun BarraNavegacion() {
  val tabs = MediodiaTardeViewModel(SavedStateHandle()).tabs

  AdelaidaTheme {
    BarraNavegacionPartida(TabData.TABLERO, tabs)
  }
}

@Composable
@Preview(name = "Barra de navegación verde - Claro")
@Preview(name = "Barra de navegación verde - Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun P3() {
  val tabs = MediodiaTardeViewModel(SavedStateHandle()).tabs

  AdelaidaThemeVerde {
    BarraNavegacionPartida(TabData.TABLERO, tabs)
  }
}