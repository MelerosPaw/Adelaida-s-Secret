package com.example.composetest.ui.viewmodel

import android.content.Context
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
import com.example.composetest.ui.compose.widget.EventoVO
import com.example.composetest.ui.contracts.ConsumidorNoche
import com.example.composetest.ui.contracts.IntencionNoche
import com.example.composetest.ui.manager.ComprobadorNombreRepetido
import com.example.composetest.ui.manager.GestorRonda
import com.example.composetest.ui.manager.InfoVisita
import com.example.composetest.ui.manager.puedeSerVisitado
import com.example.composetest.ui.viewmodel.EstadoNoche.Estado.ConfirmarEjecutarEvento
import com.example.composetest.ui.viewmodel.EstadoNoche.Estado.Eventos
import com.example.composetest.ui.viewmodel.EstadoNoche.Estado.Jugadores
import com.example.composetest.ui.viewmodel.EstadoNoche.Estado.MostrarDialogoSeleccionManualEvento
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

  val estados: EstadoNoche = EstadoNoche()
  val consumidor: ConsumidorNoche = object: ConsumidorNoche {
    override fun consumir(vararg intenciones: IntencionNoche) {
      intenciones.forEach { intencion ->
        when (intencion) {
          is IntencionNoche.AbrirDialogoBaremos -> onAbrirBaremo(intencion.jugador)
          is IntencionNoche.CerrarBaremo -> onCerrarBaremo()
          is IntencionNoche.GuardarBaremo -> partida?.id?.let { guardarBaremo(intencion.jugador, it, intencion.baremo) }
          is IntencionNoche.SeleccionarEvento -> onEventoSeleccionado(intencion.evento)
          is IntencionNoche.CerrarDialogoSeleccionManualEventos -> cerrarSelectorEventoManual(intencion.ultimoEventoVisualizado)
          is IntencionNoche.AbrirDialogoSeleccionManualEventos -> mostrarSelectorEventoManual()
          is IntencionNoche.MostrarMensaje -> mostrarMensaje(intencion.mensaje)
          is IntencionNoche.RealizarEventoSeleccionado -> onRealizarEventoSeleccionado(intencion.evento)
          is IntencionNoche.CerrarRealizacionEvento -> onCancelarRealizacionEvento()
          is IntencionNoche.SeleccionarEventoAleatorio -> onEventoAleatorio()
          is IntencionNoche.OcultarDialogoEjecucionEvento -> ocultarConfirmacionEjecutarEvento()
          is IntencionNoche.MarcarGanadorEvento -> onJugadorSeleccionado(intencion.seleccionado, intencion.jugador)
          is IntencionNoche.DarEventoPorRealizado -> onEventoRealizado(intencion.evento)
          is IntencionNoche.Visitar -> { onVisitando(intencion.jugador) }
        }
      }
    }
  }

  //region Públicos
  fun inicializar(
    partida: Partida?,
    cambioRondaSolicitado: Boolean,
    onCondicionesCambioRondaSatisfechas: ((Boolean) -> Unit),
    context: Context
  ) {
    this.partida = partida
    partida?.let {
      gestorRonda = onMensaje?.let { GestorRonda.Factory.from(partida.ronda, it) }
      estados.set(Jugadores(it.jugadores.toList()))
      inicializarEventos(it.eventosConsumidos)
      this.onCondicionesCambioRondaSatisfechas = onCondicionesCambioRondaSatisfechas
      inicializarVisitas(it.jugadores)
    }

    if (!cambioRondaSolicitado) {
      this.cambioRondaSolicitado = false
      // TODO Melero: 16/2/25 Comprobar si no se está ya haciendo las comprobaciones
    } else if (!this.cambioRondaSolicitado) {
      this.cambioRondaSolicitado = true
      comprobarSiSePuedeCambiarDeRonda(context)
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
    estados.set(EstadoNoche.Estado.EstadoDialogoBaremos(
      EstadoBaremos(jugador, baremoDelJugador, baremos, baremosDeLosDemas)))
  }

  fun onCerrarBaremo() {
    estados.set(EstadoNoche.Estado.EstadoDialogoBaremos(null))
  }

  fun guardarBaremo(jugador: Jugador, idPartida: Long, baremoSeleccionado: Baremo) {
    suspender {
      val parametros = AsignarBaremoAJugadorUC.Parametros(jugador, idPartida, baremoSeleccionado.id)
      asignarBaremoUC.get()
        .invoke(parametros)
        .usarRespuesta { onCerrarBaremo() }
    }
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
    estados.set(EstadoNoche.Estado.EventoSeleccionado(evento))

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
    estados.set(EstadoNoche.Estado.EventoRealizandose(null))
  }

  fun onRealizarEventoSeleccionado(evento: EventoVO) {
      val jugadores = estados.jugadores.value.map { JugadorVO(it, false) }
      val eventoRealizandose = EventoRealizandose(evento.evento, jugadores,
        sePuedenSeleccionarMasGanadores(jugadores, evento.evento)
      )
      estados.set(EstadoNoche.Estado.EventoRealizandose(eventoRealizandose))
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
      estados.set(EstadoNoche.Estado.EventoRealizandose(eventoActualizado))
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
  private fun comprobarSiSePuedeCambiarDeRonda(context: Context) {
    noneNull(partida, gestorRonda, onCondicionesCambioRondaSatisfechas) { partida, gestor, onComprobado ->
      onComprobado(gestor.sePuedeCambiarDeRonda(partida, context))
    }
  }

  private fun mostrarConfirmacionEjecutarEvento(evento: EventoVO?) {
    estados.set(ConfirmarEjecutarEvento(evento))
  }

  private fun sePuedenSeleccionarMasGanadores(jugadores: List<JugadorVO>, evento: Evento): Boolean =
    jugadores.count { it.seleccionado } < evento.maxGanadores.obtenerCantidadGanadores()

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
    estados.set(EstadoNoche.Estado.EventoSeleccionado(partida?.eventoActual?.let {
      EventoVO(
        evento = it,
        seleccionable = true,
        sePuedeEjecutarAhora = gestorRonda?.seEjecutaAhora(it) == true
      )
    }))
  }

  private fun setEventoYaRealizado() {
    estados.set(EstadoNoche.Estado.YaSeHaRealizado(partida?.eventoActualEjecutado == true))
  }

  private fun inicializarVisitas(jugadores: Array<Jugador>) {
    suspender {
      val infos = jugadores.map {
        val validaciones = puedeSerVisitado(it)
        InfoVisita.Jugador(it, validaciones)
      }

      val visitas = if (infos.isEmpty()) {
        InfoVisita.NadieParaVisitar
      } else {
        InfoVisita.Info(infos)
      }

      estados.set(EstadoNoche.Estado.VisitasAdelaida(visitas))
    }
  }

  private fun onVisitando(jugador: Jugador) {
//    estados.set(EstadoNoche.Estado.Visitando(jugador))
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

class EstadoNoche(
  jugadores: Jugadores = Jugadores(emptyList()),
  estadoDialogoBaremos: Estado.EstadoDialogoBaremos = Estado.EstadoDialogoBaremos(null),
  eventos: Eventos = Eventos(emptyList()),
  eventoSeleccionado: Estado.EventoSeleccionado = Estado.EventoSeleccionado(null),
  mostrarDialogoSeleccionManualEvento: MostrarDialogoSeleccionManualEvento = MostrarDialogoSeleccionManualEvento(false, null),
  confirmarEjecutarEvento: ConfirmarEjecutarEvento = ConfirmarEjecutarEvento(null),
  eventoRealizandose: Estado.EventoRealizandose = Estado.EventoRealizandose(null),
  yaSeHaRealizado: Estado.YaSeHaRealizado = Estado.YaSeHaRealizado(false),
  visitaAdelaidaInfo: Estado.VisitasAdelaida = Estado.VisitasAdelaida(InfoVisita.Cargando)
) {

  private val _jugadores: MutableState<List<Jugador>> = mutableStateOf(jugadores.jugadores)
  private val _estadoDialogoBaremos: MutableState<EstadoBaremos?> = mutableStateOf(estadoDialogoBaremos.estadoBaremos)
  private val _eventos: MutableState<List<EventoVO>> = mutableStateOf(eventos.eventos)
  private val _eventoSeleccionado: MutableState<EventoVO?> = mutableStateOf(eventoSeleccionado.evento)
  private val _mostrarDialogoSeleccionManualEvento: MutableState<MostrarDialogoSeleccionManualEvento> = mutableStateOf(mostrarDialogoSeleccionManualEvento)
  private val _confirmarEjecutarEvento: MutableState<EventoVO?> = mutableStateOf(confirmarEjecutarEvento.evento)
  private val _eventoRealizandose: MutableState<EventoRealizandose?> = mutableStateOf(eventoRealizandose.evento)
  private val _yaSeHaRealizado: MutableState<Boolean> = mutableStateOf(yaSeHaRealizado.realizado)
  private val _visitasAdelaida: MutableState<InfoVisita> = mutableStateOf(visitaAdelaidaInfo.info)

  val jugadores: State<List<Jugador>> = _jugadores
  val estadoDialogoBaremos: State<EstadoBaremos?> = _estadoDialogoBaremos
  val eventos: State<List<EventoVO>> = _eventos
  val eventoSeleccionado: State<EventoVO?> = _eventoSeleccionado
  val mostrarDialogoSeleccionManualEvento: State<MostrarDialogoSeleccionManualEvento> = _mostrarDialogoSeleccionManualEvento
  val confirmarEjecutarEvento: State<EventoVO?> = _confirmarEjecutarEvento
  val eventoRealizandose: State<EventoRealizandose?> = _eventoRealizandose
  val yaSeHaRealizado: State<Boolean> = _yaSeHaRealizado
  val visitaAdelaida: State<InfoVisita> = _visitasAdelaida

  fun set(estado: Estado) {
    estado.set(this)
  }

  sealed class Estado {

    abstract fun set(estados: EstadoNoche)

    class Jugadores(val jugadores: List<Jugador>): Estado() {
      override fun set(estados: EstadoNoche) {
        estados._jugadores.value = jugadores
      }
    }
    class EstadoDialogoBaremos(val estadoBaremos: EstadoBaremos?): Estado() {
      override fun set(estados: EstadoNoche) {
        estados._estadoDialogoBaremos.value = estadoBaremos
      }
    }
    class Eventos(val eventos: List<EventoVO>): Estado() {
      override fun set(estados: EstadoNoche) {
        estados._eventos.value = eventos
      }
    }
    class EventoSeleccionado(val evento: EventoVO?): Estado() {
      override fun set(estados: EstadoNoche) {
        estados._eventoSeleccionado.value = evento
      }
    }
    data class MostrarDialogoSeleccionManualEvento(val mostrar: Boolean, val ultimoEventoVisto: EventoVO?): Estado() {
      override fun set(estados: EstadoNoche) {
        estados._mostrarDialogoSeleccionManualEvento.value = this
      }
    }
    class ConfirmarEjecutarEvento(val evento: EventoVO?): Estado() {
      override fun set(estados: EstadoNoche) {
        estados._confirmarEjecutarEvento.value = evento
      }
    }
    class EventoRealizandose(val evento: NocheViewModel.EventoRealizandose?): Estado() {
      override fun set(estados: EstadoNoche) {
        estados._eventoRealizandose.value = evento
      }
    }
    class YaSeHaRealizado(val realizado: Boolean): Estado() {
      override fun set(estados: EstadoNoche) {
        estados._yaSeHaRealizado.value = realizado
      }
    }
    class VisitasAdelaida(val info: InfoVisita): Estado() {
      override fun set(estados: EstadoNoche) {
        estados._visitasAdelaida.value = info
      }
    }
  }
}

class EstadoBaremos(
  val jugador: Jugador,
  val baremoSeleccionado: Baremo?,
  val baremos: List<Baremo>,
  val baremosNoSeleccionables: List<Baremo>
)