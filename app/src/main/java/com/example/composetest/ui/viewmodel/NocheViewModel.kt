package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.example.composetest.data.uc.AsignarBaremoAJugadorUC
import com.example.composetest.data.uc.EjecutarEventoUC
import com.example.composetest.data.uc.ObtenerEventosUC
import com.example.composetest.data.uc.SeleccionarEventoUC
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.Baremo
import com.example.composetest.model.CreadorBaremos
import com.example.composetest.model.Evento
import com.example.composetest.model.Jugador
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.screen.EstadoBaremos
import com.example.composetest.ui.compose.widget.EventoVO
import com.example.composetest.ui.contracts.Consumidor
import com.example.composetest.ui.contracts.Intencion
import com.example.composetest.ui.manager.ComprobadorNombreRepetido
import com.example.composetest.ui.manager.GestorRonda
import com.example.composetest.ui.viewmodel.Estados.Estado.ConfirmarEjecutarEvento
import com.example.composetest.ui.viewmodel.Estados.Estado.Eventos
import com.example.composetest.ui.viewmodel.Estados.Estado.Jugadores
import com.example.composetest.ui.viewmodel.Estados.Estado.MostrarDialogoSeleccionManualEvento
import com.example.composetest.ui.viewmodel.NocheViewModel.EventoRealizandose
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider
import kotlin.random.Random

@HiltViewModel
class NocheViewModel @Inject constructor(val savedStateHandle: SavedStateHandle) : BaseViewModel() {

  @Inject
  lateinit var asignarBaremoUC: Provider<AsignarBaremoAJugadorUC>
  @Inject
  lateinit var obtenerEventosUC: Provider<ObtenerEventosUC>
  @Inject
  lateinit var seleccionarEventoUC: Provider<SeleccionarEventoUC>
  @Inject
  lateinit var ejecutarEventoUC: Provider<EjecutarEventoUC>

  private var partida: Partida? = null
  private var gestorRonda: GestorRonda? = null
  private val baremos by lazy { CreadorBaremos().crearBaremos() }
  private var cambioRondaSolicitado: Boolean = false
  private var onCondicionesCambioRondaSatisfechas: ((Boolean) -> Unit)? = null

  val estados: Estados = Estados()
  val consumidor: Consumidor = object: Consumidor {
    override fun consumir(vararg intenciones: Intencion) {
      intenciones.forEach { intencion ->
        when (intencion) {
          is Intencion.AbrirDialogoBaremos -> onAbrirBaremo(intencion.jugador)
          is Intencion.CerrarBaremo -> onCerrarBaremo()
          is Intencion.GuardarBaremo -> partida?.id?.let { guardarBaremo(intencion.jugador, it, intencion.baremo) }
          is Intencion.SeleccionarEvento -> onEventoSeleccionado(intencion.evento)
          is Intencion.CerrarDialogoSeleccionManualEventos -> cerrarSelectorEventoManual(intencion.ultimoEventoVisualizado)
          is Intencion.AbrirDialogoSeleccionManualEventos -> mostrarSelectorEventoManual()
          is Intencion.MostrarMensaje -> mostrarMensaje(intencion.mensaje)
          is Intencion.RealizarEventoSeleccionado -> onRealizarEventoSeleccionado(intencion.evento)
          is Intencion.CerrarRealizacionEvento -> onCancelarRealizacionEvento()
          is Intencion.SeleccionarEventoAleatorio -> onEventoAleatorio()
          is Intencion.OcultarDialogoEjecucionEvento -> ocultarConfirmacionEjecutarEvento()
          is Intencion.MarcarGanadorEvento -> onJugadorSeleccionado(intencion.seleccionado, intencion.jugador)
          is Intencion.DarEventoPorRealizado -> onEventoRealizado(intencion.evento)
        }
      }
    }
  }

  //region Públicos
  fun inicializar(partida: Partida?, cambioRondaSolicitado: Boolean, onCondicionesCambioRondaSatisfechas: ((Boolean) -> Unit)) {
    this.partida = partida
    partida?.let {
      gestorRonda = onMensaje?.let { GestorRonda.Factory.from(partida.ronda, it) }
      estados.set(Jugadores(it.jugadores.toList()))
      inicializarEventos(it.eventosConsumidos)
      this.onCondicionesCambioRondaSatisfechas = onCondicionesCambioRondaSatisfechas
    }

    if (!cambioRondaSolicitado) {
      this.cambioRondaSolicitado = false
      // TODO Melero: 16/2/25 Comprobar si no se está ya haciendo las comprobaciones
    } else if (!this.cambioRondaSolicitado) {
      this.cambioRondaSolicitado = true
      comprobarSiSePuedeCambiarDeRonda()
    }
  }

  fun comprobarNombreRepetido(nombre: String): Boolean =
    ComprobadorNombreRepetido(estados.jugadores.value).estaRepetido(nombre)

  fun onAbrirBaremo(jugador: Jugador) {
    val baremoDelJugador =
      jugador.idBaremo?.let { seleccionado -> baremos.find { it.id == seleccionado } }
    val baremosDeLosDemas = estados.jugadores.value.mapNotNull {
      it.takeIf { it.noEsElMismoQue(jugador) }
        ?.idBaremo
        ?.let(Baremo::empty)
    }
    estados.set(Estados.Estado.EstadoDialogoBaremos(
      EstadoBaremos(jugador, baremoDelJugador, baremos, baremosDeLosDemas)))
  }

  fun onCerrarBaremo() {
    estados.set(Estados.Estado.EstadoDialogoBaremos(null))
  }

  fun guardarBaremo(jugador: Jugador, idPartida: Long, baremoSeleccionado: Baremo) {
    suspender {
      val parametros = AsignarBaremoAJugadorUC.Parametros(jugador, idPartida, baremoSeleccionado.id)
      asignarBaremoUC.get()
        .invoke(parametros)
        .usarRespuesta { onCerrarBaremo() }
    }
  }

  private fun inicializarEventos(eventosConsumidos: Set<Evento>) {
    if (estados.eventos.value.isEmpty()) {
      cargarListaEventos(eventosConsumidos)
    } else {
      pintarEventoSeleccionadoYEstadoRealizacion()
    }
  }

  private fun cargarListaEventos(eventosConsumidos: Set<Evento>) {
    suspender {
      obtenerEventosUC.get()
        .invoke(ObtenerEventosUC.Parametros())
        .usarRespuesta {
          setListaEventos(it, eventosConsumidos)
          pintarEventoSeleccionadoYEstadoRealizacion()
        }
    }
  }

  private fun pintarEventoSeleccionadoYEstadoRealizacion() {
    setEventoSeleccionado()
    // TODO Melero: 17/4/25 Ver en el flow cuándo cambia de true a false cuando realizo el evento
    setEventoYaRealizado()
    // TODO Melero: 17/4/25 Justo en el paso anterior, antes de cambiar de ronda, hay que poner esto a false.
  }

  fun cerrarSelectorEventoManual(ultimoEventoVisto: EventoVO?) {
    estados.set(MostrarDialogoSeleccionManualEvento(false, ultimoEventoVisto))
  }

  fun mostrarSelectorEventoManual() {
    estados.set(estados.mostrarDialogoSeleccionManualEvento.value.copy(true))
  }

  fun onEventoAleatorio() {
    estados.eventos.value
      .filter { it != estados.eventoSeleccionado.value }
      .let {
        onEventoSeleccionado(it[Random.nextInt(it.size)], false)
      }
  }

  fun onEventoSeleccionado(evento: EventoVO, preguntarSiSeQuiereEjecutarTrasSeleccionar: Boolean = true) {
    val eventoSeleccionadoActual = estados.eventoSeleccionado.value
    estados.set(Estados.Estado.EventoSeleccionado(evento))

    suspender {
      partida?.let {
        seleccionarEventoUC
          .get()
          .invoke(SeleccionarEventoUC.Parametros(it.id, evento.evento))
          .usarRespuesta {
            if (it) {
              afterEventoSeleccionado(evento, eventoSeleccionadoActual, preguntarSiSeQuiereEjecutarTrasSeleccionar)
            }
          }
      }
    }
  }

  fun onCancelarRealizacionEvento() {
    estados.set(Estados.Estado.EventoRealizandose(null))
  }

  fun onRealizarEventoSeleccionado(evento: EventoVO) {
      val jugadores = estados.jugadores.value.map { JugadorVO(it, false) }
      val eventoRealizandose = EventoRealizandose(evento.evento, jugadores,
        sePuedenSeleccionarMasGanadores(jugadores, evento.evento)
      )
      estados.set(Estados.Estado.EventoRealizandose(eventoRealizandose))
  }

  fun ocultarConfirmacionEjecutarEvento() {
    mostrarConfirmacionEjecutarEvento(null)
  }

  fun onJugadorSeleccionado(seleccionado: Boolean, jugador: JugadorVO) {
    estados.eventoRealizandose.value?.let {
      val jugadores = it.jugadores.map {
        it.takeUnless { it esElMismoQue jugador } ?: JugadorVO(it.jugador, seleccionado)
      }
      val puedeSeleccionarMasJugadores = sePuedenSeleccionarMasGanadores(jugadores, it.evento)
      val eventoActualizado = it.copy(
        jugadores = jugadores,
        puedeSeleccionarMasJugadores = puedeSeleccionarMasJugadores
      )
      estados.set(Estados.Estado.EventoRealizandose(eventoActualizado))
    }
  }

  fun onEventoRealizado(eventoRealizandose: EventoRealizandose) {
    suspender {
      onCancelarRealizacionEvento()
      partida?.id?.let {
        ejecutarEventoUC
          .get()
          .invoke(
            EjecutarEventoUC.Parametros(
              idPartida = it,
              evento = eventoRealizandose.evento,
              ganadores = eventoRealizandose.jugadores.mapNotNull { jugador ->
                jugador.jugador.takeIf { jugador.seleccionado }
              }
            )
          )
      }
    }
  }
  //endregion

  //region Privados
  private fun comprobarSiSePuedeCambiarDeRonda() {
    noneNull(partida, gestorRonda, onCondicionesCambioRondaSatisfechas) { partida, gestor, onComprobado ->
      onComprobado(gestor.sePuedeCambiarDeRonda(partida))
    }
  }

  private fun mostrarConfirmacionEjecutarEvento(evento: EventoVO?) {
    estados.set(ConfirmarEjecutarEvento(evento))
  }

  private fun sePuedenSeleccionarMasGanadores(
    jugadores: List<JugadorVO>,
    evento: Evento
  ): Boolean = jugadores.count { it.seleccionado } < evento.maxGanadores.obtenerCantidadGanadores()

  private fun afterEventoSeleccionado(
    evento: EventoVO,
    eventoSeleccionadoActual: EventoVO?,
    preguntarSiSeQuiereEjecutarTrasSeleccionar: Boolean = true
  ) {
    if (gestorRonda?.seEjecutaAhora(evento.evento) == true && preguntarSiSeQuiereEjecutarTrasSeleccionar) {
      mostrarConfirmacionEjecutarEvento(evento)
    } else if (eventoSeleccionadoActual == evento) {
      mostrarMensaje(Mensaje("El evento se llevará a cabo en su debido momento."))
    } else {
      cerrarSelectorEventoManual(evento)
    }
  }

  private fun setListaEventos(
    eventos: List<Evento>,
    eventosConsumidos: Set<Evento>
  ) {
    estados.set(Eventos(eventos.map {
      EventoVO(
        evento = it,
        seleccionable = it !in eventosConsumidos,
        sePuedeEjecutarAhora = gestorRonda?.seEjecutaAhora(it) == true
      )
    }))
  }

  private fun setEventoSeleccionado() {
    estados.set(Estados.Estado.EventoSeleccionado(partida?.eventoActual?.let {
      EventoVO(
        evento = it,
        seleccionable = true,
        sePuedeEjecutarAhora = gestorRonda?.seEjecutaAhora(it) == true
      )
    }))
  }

  private fun setEventoYaRealizado() {
    estados.set(Estados.Estado.YaSeHaRealizado(partida?.eventoActualEjecutado == true))
  }
  //endregion

  class JugadorVO(val jugador: Jugador, val seleccionado: Boolean) {

    infix fun esElMismoQue(otro: JugadorVO): Boolean = jugador.esElMismoQue(otro.jugador)
  }

  data class EventoRealizandose(
    val evento: Evento,
    val jugadores: List<JugadorVO>,
    val puedeSeleccionarMasJugadores: Boolean
  )
}

class Estados(
  jugadores: Jugadores = Jugadores(emptyList()),
  estadoDialogoBaremos: Estado.EstadoDialogoBaremos = Estado.EstadoDialogoBaremos(null),
  eventos: Eventos = Eventos(emptyList()),
  eventoSeleccionado: Estado.EventoSeleccionado = Estado.EventoSeleccionado(null),
  mostrarDialogoSeleccionManualEvento: MostrarDialogoSeleccionManualEvento = MostrarDialogoSeleccionManualEvento(false, null),
  confirmarEjecutarEvento: ConfirmarEjecutarEvento = ConfirmarEjecutarEvento(null),
  eventoRealizandose: Estado.EventoRealizandose = Estado.EventoRealizandose(null),
  yaSeHaRealizado: Estado.YaSeHaRealizado = Estado.YaSeHaRealizado(false),
) {

  private val _jugadores: MutableState<List<Jugador>> = mutableStateOf(jugadores.jugadores)
  private val _estadoDialogoBaremos: MutableState<EstadoBaremos?> = mutableStateOf(estadoDialogoBaremos.estadoBaremos)
  private val _eventos: MutableState<List<EventoVO>> = mutableStateOf(eventos.eventos)
  private val _eventoSeleccionado: MutableState<EventoVO?> = mutableStateOf(eventoSeleccionado.evento)
  private val _mostrarDialogoSeleccionManualEvento: MutableState<MostrarDialogoSeleccionManualEvento> = mutableStateOf(mostrarDialogoSeleccionManualEvento)
  private val _confirmarEjecutarEvento: MutableState<EventoVO?> = mutableStateOf(confirmarEjecutarEvento.evento)
  private val _eventoRealizandose: MutableState<EventoRealizandose?> = mutableStateOf(eventoRealizandose.evento)
  private val _yaSeHaRealizado: MutableState<Boolean> = mutableStateOf(yaSeHaRealizado.realizado)

  val jugadores: State<List<Jugador>> = _jugadores
  val estadoDialogoBaremos: State<EstadoBaremos?> = _estadoDialogoBaremos
  val eventos: State<List<EventoVO>> = _eventos
  val eventoSeleccionado: State<EventoVO?> = _eventoSeleccionado
  val mostrarDialogoSeleccionManualEvento: State<MostrarDialogoSeleccionManualEvento> = _mostrarDialogoSeleccionManualEvento
  val confirmarEjecutarEvento: State<EventoVO?> = _confirmarEjecutarEvento
  val eventoRealizandose: State<EventoRealizandose?> = _eventoRealizandose
  val yaSeHaRealizado: State<Boolean> = _yaSeHaRealizado

  fun set(estado: Estado) {
    estado.set(this)
  }

  sealed class Estado {

    abstract fun set(estados: Estados)

    class Jugadores(val jugadores: List<Jugador>): Estado() {
      override fun set(estados: Estados) {
        estados._jugadores.value = jugadores
      }
    }
    class EstadoDialogoBaremos(val estadoBaremos: EstadoBaremos?): Estado() {
      override fun set(estados: Estados) {
        estados._estadoDialogoBaremos.value = estadoBaremos
      }
    }
    class Eventos(val eventos: List<EventoVO>): Estado() {
      override fun set(estados: Estados) {
        estados._eventos.value = eventos
      }
    }
    class EventoSeleccionado(val evento: EventoVO?): Estado() {
      override fun set(estados: Estados) {
        estados._eventoSeleccionado.value = evento
      }
    }
    data class MostrarDialogoSeleccionManualEvento(val mostrar: Boolean, val ultimoEventoVisto: EventoVO?): Estado() {
      override fun set(estados: Estados) {
        estados._mostrarDialogoSeleccionManualEvento.value = this
      }
    }
    class ConfirmarEjecutarEvento(val evento: EventoVO?): Estado() {
      override fun set(estados: Estados) {
        estados._confirmarEjecutarEvento.value = evento
      }
    }
    class EventoRealizandose(val evento: NocheViewModel.EventoRealizandose?): Estado() {
      override fun set(estados: Estados) {
        estados._eventoRealizandose.value = evento
      }
    }
    class YaSeHaRealizado(val realizado: Boolean): Estado() {
      override fun set(estados: Estados) {
        estados._yaSeHaRealizado.value = realizado
      }
    }
  }
}