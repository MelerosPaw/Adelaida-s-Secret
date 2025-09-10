package com.example.composetest.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.model.Partida
import com.example.composetest.model.Tablero
import com.example.composetest.ui.compose.AccionProhibida
import com.example.composetest.ui.compose.EstadoAccionProhibida
import com.example.composetest.ui.compose.PosibleAccionProhibida
import com.example.composetest.ui.compose.TabData
import com.example.composetest.ui.compose.TabWrapper
import com.example.composetest.ui.compose.getTabs
import com.example.composetest.ui.contracts.ConsumidorTabInfo
import com.example.composetest.ui.contracts.EstadoMediodiaTarde
import com.example.composetest.ui.contracts.IntencionTabInfo
import com.example.composetest.ui.manager.AsuntoTurbio
import com.example.composetest.ui.manager.GestorRonda
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MediodiaTardeViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    // region Estados
    var estados: EstadoMediodiaTarde? = null
    private val _paginaActual: MutableState<TabData> = mutableStateOf(TabData.TABLERO)
    val paginaActual: State<TabData> = _paginaActual
    private val _tablero: MutableStateFlow<EstadoTablero?> = MutableStateFlow(null)
    val tablero: StateFlow<EstadoTablero?> = _tablero
    private val _jugadores: MutableStateFlow<List<Jugador>?> = MutableStateFlow(null)
    val jugadores: StateFlow<List<Jugador>?> = _jugadores
    private val _estadoTablero: MutableState<EstadoTablero?> = mutableStateOf(null)
    val estadoTablero: State<EstadoTablero?> = _estadoTablero
    private var _asuntoTurbio: MutableState<AsuntoTurbio> = mutableStateOf(AsuntoTurbio.Ninguno())
    val asuntoTurbio: State<AsuntoTurbio> = _asuntoTurbio
    val sePuedeComprar: State<Boolean> = mutableStateOf(false)
    private val _estadoAccionProhibida: MutableState<EstadoAccionProhibida?> = mutableStateOf(null)
    val estadoAccionProhibida: State<EstadoAccionProhibida?> = _estadoAccionProhibida
    // endregion

    val consumidorTabInfo = object: ConsumidorTabInfo {
        override fun consumir(vararg intenciones: IntencionTabInfo) {
            intenciones.forEach { intencion ->
                when(intencion) {
                  is IntencionTabInfo.MostrarMensaje -> onMensaje?.invoke(intencion.mensaje)
                  IntencionTabInfo.MostrarDialogoSalir -> onMostrarMensajeAbandonar?.invoke()
                }
            }
        }
    }
    val tabs: Array<TabWrapper> = getTabs(::cambiarPaginaActual)
    private var partida: Partida? = null
    private var gestorRonda: GestorRonda? = null
    private var onMostrarPapelera: ((mostrar: Boolean) -> Unit)? = null
    private var cambioRondaSolicitado: Boolean = false
    private var onCondicionesCambioRondaSatisfechas: ((Boolean) -> Unit)? = null
    private var onMostrarMensajeAbandonar: (() -> Unit)? = null

    //region Públicos
    fun inicializar(
        partida: Partida,
        onMostrarPapelera: (Boolean) -> Unit,
        cambioRondaSolicitado: Boolean,
        onCondicionesCambioRondaSatisfechas: (Boolean) -> Unit,
        onMostrarMensajeAbandonar: () -> Unit,
        context: Context
    ) {
        this.estados = EstadoMediodiaTarde(partida)
        this.partida = partida
        this.onMostrarPapelera = onMostrarPapelera
        this.onCondicionesCambioRondaSatisfechas = onCondicionesCambioRondaSatisfechas
        this.onMostrarMensajeAbandonar = onMostrarMensajeAbandonar
        val fueraDelTablero = obtenerElementosFueraDelTablero(partida)

        _jugadores.value = partida.jugadores.toList()
        _estadoTablero.value = partida.tablero?.let { EstadoTablero(it, fueraDelTablero) }

        inicializarGestorRonda(partida)

        if (!cambioRondaSolicitado) {
            this.cambioRondaSolicitado = false
            // TODO Melero: 16/2/25 Comprobar si no se está ya haciendo las comprobaciones
        } else if (!this.cambioRondaSolicitado) {
            this.cambioRondaSolicitado = true
            comprobarSiSePuedeCambiarDeRonda(context)
        }
    }

    fun iniciarAsuntoTurbio(asuntoTurbio: AsuntoTurbio) {
        _asuntoTurbio.value = asuntoTurbio
    }

    fun onAccionProhibida(accionProhida: AccionProhibida?) {
        if (accionProhida?.posibleAccionProhibida == null) {
            ocultarDialogoAccionProhibida()

        } else {
            val posibleAccionProhibida = (accionProhida.posibleAccionProhibida as? PosibleAccionProhibida.Reasignacion)
                ?.copy(tabActual = _paginaActual.value)
                ?: accionProhida.posibleAccionProhibida

            val advertencia: String? = gestorRonda
                ?.advertenciaAccionProhibida(posibleAccionProhibida)
                ?.let(context::getString)

            ejecutarAccionProhibida(advertencia, accionProhida)
        }
    }

    fun cambiarATarde(partida: Partida) {
        inicializarGestorRonda(partida)
    }
    // endregion

    // region Privados
    private fun inicializarGestorRonda(partida: Partida) {
        val gestorRonda = onMensaje?.let { GestorRonda.Factory.from(partida.ronda, it) }

        if (this.gestorRonda?.javaClass != gestorRonda?.javaClass) {
            this.gestorRonda = gestorRonda?.also {
                cambiarPaginaActual(it.getTabInicial())
            }
        }
    }

    private fun ocultarDialogoAccionProhibida() {
        _estadoAccionProhibida.value = null
    }

    private fun obtenerElementosFueraDelTablero(partida: Partida): List<ElementoTablero> =
        partida.jugadores.flatMap {
            it.cartas() + it.pistas()
        }

    private fun cambiarPaginaActual(pagina: TabData) {
        if (paginaActual.value != pagina) {
            onAccionProhibida(
                AccionProhibida(
                    posibleAccionProhibida = PosibleAccionProhibida.CambioTab(pagina),
                    onSePermite = {
                        _paginaActual.value = pagina
                        onMostrarPapelera?.invoke(pagina == TabData.JUGADORES)
                    },
                    onNoSePermite = {}
                )
            )
        }
    }

    private fun ejecutarAccionProhibida(advertencia: String?, accionProhibida: AccionProhibida) {
        advertencia?.let {
            _estadoAccionProhibida.value = EstadoAccionProhibida(it,
                AccionProhibida(
                    accionProhibida.posibleAccionProhibida,
                    { ejecutarAccionProhibida(null, accionProhibida) },
                    {
                        accionProhibida.onNoSePermite()
                        ocultarDialogoAccionProhibida()
                    })
            )
        } ?: run {
            accionProhibida.onSePermite()
            ocultarDialogoAccionProhibida()
        }
    }

    private fun comprobarSiSePuedeCambiarDeRonda(context: Context) {
        noneNull(partida, gestorRonda, onCondicionesCambioRondaSatisfechas) {
            partida, gestor, onComprobado -> onComprobado(gestor.sePuedeCambiarDeRonda(partida, context))
        }
    }

    // endregion

    // region Clases anidadas
    class EstadoTablero(
        val tablero: Tablero,
        val elementosFueraDelTablero: List<ElementoTablero>
    )
    // endregion
}