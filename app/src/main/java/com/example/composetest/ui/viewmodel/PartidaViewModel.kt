package com.example.composetest.ui.viewmodel

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.composetest.data.uc.ActualizarNombrePartidaUC
import com.example.composetest.data.uc.PasarSiguienteRondaUC
import com.example.composetest.data.uc.ObtenerPartidaFlowUC
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.model.Partida
import com.example.composetest.model.Partida.Ronda
import com.example.composetest.ui.compose.AccionProhibida
import com.example.composetest.ui.compose.EstadoAccionProhibida
import com.example.composetest.ui.compose.PosibleAccionProhibida
import com.example.composetest.ui.compose.TabData
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.navegacion.NavegadorRondas
import com.example.composetest.ui.contracts.ConsumidorPartida
import com.example.composetest.ui.contracts.EstadoPartida
import com.example.composetest.ui.contracts.IntencionPartida
import com.example.composetest.ui.manager.AsuntoTurbio
import com.example.composetest.ui.manager.GestorRonda
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import javax.inject.Provider

typealias PartidaNavegacion = com.example.composetest.ui.compose.navegacion.Partida

@HiltViewModel
class PartidaViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    @Inject
    lateinit var actualizarNombrePartidaUC: Provider<ActualizarNombrePartidaUC>
    @Inject
    lateinit var obtenerPartidaFlowUC: Provider<ObtenerPartidaFlowUC>
    @Inject
    lateinit var actualizarRondaUC: Provider<PasarSiguienteRondaUC>

    // region Estados
    var estado: EstadoPartida = EstadoPartida()
    private val _idPartida: MutableState<Long?> = mutableStateOf(null)
    val idPartida: State<Long?> = _idPartida
    private val _filtrosAbiertos: MutableState<Boolean> = mutableStateOf(false)
    val filtrosAbiertos: State<Boolean> = _filtrosAbiertos
    private var _asuntoTurbio: MutableState<AsuntoTurbio> = mutableStateOf(AsuntoTurbio.Ninguno())
    val asuntoTurbio: State<AsuntoTurbio> = _asuntoTurbio
    val sePuedeComprar: State<Boolean> = mutableStateOf(false)
    private val _mostrarIconoPapelera: MutableState<Boolean> = mutableStateOf(false)
    val mostrarIconoPapelera: State<Boolean> = _mostrarIconoPapelera
    private val _mostrarPapelera: MutableState<Boolean> = mutableStateOf(false)
    val mostrarPapelera: State<Boolean> = _mostrarPapelera
    private val _mostrarDialogoAbandonar: MutableState<Boolean> = mutableStateOf(false)
    val mostrarDialogoAbandonar: State<Boolean> = _mostrarDialogoAbandonar
    // endregion

    var onAbandonar: (() -> Unit)? = null
    var navegadorRondas: NavegadorRondas? = null
    var gestorRonda: GestorRonda? = null
    val consumidor: ConsumidorPartida = object: ConsumidorPartida {
        override fun consumir(vararg intenciones: IntencionPartida) {
            intenciones.forEach {
                when (it) {
                    is IntencionPartida.MostrarMensaje -> onMensaje?.invoke(it.mensaje)
                    is IntencionPartida.IniciarAsuntoTurbio -> iniciarAsuntoTurbio(it.asuntoTurbio)
                    IntencionPartida.MostrarDialogoAbandonar -> onMostrarDialogoAbandonarCambiado(true)
                    is IntencionPartida.CambiarTab -> cambiarPaginaActual(it.nuevaTab)
                    is IntencionPartida.TratarAccionProhibida -> onAccionProhibida(it.accionProhibida)
                }
            }
        }
    }

    // region Públicos
    fun cargarPartida() {
        val params = savedStateHandle.toRoute<PartidaNavegacion>()
        obtenerPartida(params.idPartida)
    }

    fun onCambioRondaSolicitado(infoRondaActual: InfoRonda, context: Context) {
        noneNull(estado.partida.value.partida, gestorRonda) { partida, gestor ->
            onCondicionesCambioRondaSatisfechas(
                gestor.sePuedeCambiarDeRonda(partida, context),
                infoRondaActual
            )
        }
    }

    fun onCondicionesCambioRondaSatisfechas(satisfechas: Boolean, infoRondaActual: InfoRonda) {
        if (satisfechas) {
            onMostrarDialogoCambioRonda(infoRondaActual)
        }
    }

    fun onMostrarDialogoCambioRonda(info: InfoRonda) {
        estado.set(EstadoPartida.Estado.EstadoInfoRonda(estado.infoRonda.value.infoRonda?.copy(
            mostrarDialogoSiguienteRonda = !info.mostrarDialogoSiguienteRonda,
        )))
    }

    fun onCambiarVisibilidadExplicacionRonda(info: InfoRonda) {
        estado.set(EstadoPartida.Estado.EstadoInfoRonda(estado.infoRonda.value.infoRonda?.copy(
            explicacionVisible = !info.explicacionVisible))
        )
    }

    fun onCerrarTituloSiguienteRonda(info: InfoRonda) {
        estado.set(EstadoPartida.Estado.EstadoInfoRonda(estado.infoRonda.value.infoRonda?.copy(
            mostrarTituloSiguienteRonda = false, explicacionVisible = true))
        )
    }

    fun actualizarNombrePartida(nuevoNombre: String) {
        _idPartida.value?.let {
            suspender {
                actualizarNombrePartidaUC.get()
                    .invoke(ActualizarNombrePartidaUC.Parametros(it, nuevoNombre))

            }
        }
    }

    fun onNavegacionHabilitada(navegadorRondas: NavegadorRondas) {
        this.navegadorRondas = navegadorRondas
    }

    fun abrirFiltros() {
        _filtrosAbiertos.value = true
    }

    fun cerrarFiltros() {
        _filtrosAbiertos.value = false
    }

    fun cancelarCompra() {
        _asuntoTurbio.value = AsuntoTurbio.Ninguno()
    }

    fun iniciarAsuntoTurbio(asuntoTurbio: AsuntoTurbio) {
        _asuntoTurbio.value = asuntoTurbio
    }

    fun onMostrarIconoPapelera(mostrar: Boolean) {
//        _mostrarIconoPapelera.value = mostrar
        // De momento la papelera no tiene sentido
        _mostrarIconoPapelera.value = false
    }

    fun abrirPapelera() {
        _mostrarPapelera.value = true
    }

    fun cerrarPapelera() {
        _mostrarPapelera.value = false
    }

    fun recuperarElemento(elemento: ElementoTablero, jugador: Jugador) {
        mostrarMensaje(Mensaje("Devolver ${elemento::class.simpleName} a ${jugador.nombre}"))
    }

    fun onRondaSiguiente() {
        estado.partida.value.partida?.let {
            suspender {
                val params = PasarSiguienteRondaUC.Parametros(it)
                actualizarRondaUC.get()
                    .invoke(params)
                    .usarRespuesta { navegarASiguienteRonda(it) }
            }
        }
    }

    fun onMostrarDialogoAbandonarCambiado(mostrar: Boolean) {
        _mostrarDialogoAbandonar.value = mostrar
    }

    fun abandonar() {
        onMostrarDialogoAbandonarCambiado(false)
        onAbandonar?.invoke()
    }
    // endregion

    // region Privados
    private fun obtenerPartida(partida: Long) {
        val partidaActual = _idPartida.value

        if (partidaActual == null || partidaActual != partida) {
            _idPartida.value = partida

            suspender {
                val params = ObtenerPartidaFlowUC.Parametros(partida)
                obtenerPartidaFlowUC.get()
                    .invoke(params)
                    .collectLatest { respuesta ->
                        respuesta.usarRespuesta { partida -> onPartidaActualizada(partida) }
                    }
            }
        }
    }

    private fun onPartidaActualizada(partida: Partida?) {
        partida?.let {
            val haCambiadoDeRonda = it.ronda != estado.infoRonda.value.infoRonda?.ronda
            initGestorRonda(it, haCambiadoDeRonda)

            gestorRonda?.let { gestor ->
                estado.setPartida(it, haCambiadoDeRonda, gestor)
            }
        }
    }

    private fun initGestorRonda(partida: Partida, haCambiadoDeRonda: Boolean) {
        if (gestorRonda == null || haCambiadoDeRonda) {
            gestorRonda = GestorRonda.Factory.from(partida.ronda) { mensaje ->
                consumidor.consumir(IntencionPartida.MostrarMensaje(mensaje))
            }
        }
    }

    private fun navegarASiguienteRonda(partida: Partida) {
        navegadorRondas?.navegarARondaActual(partida)
    }

    private fun onAccionProhibida(accionProhida: AccionProhibida?) {
        if (accionProhida?.posibleAccionProhibida == null) {
            ocultarDialogoAccionProhibida()

        } else {
            val posibleAccionProhibida = (accionProhida.posibleAccionProhibida as? PosibleAccionProhibida.Reasignacion)
                ?.copy(tabActual = estado.tabActual.value.tab!!)
                ?: accionProhida.posibleAccionProhibida

            val advertencia: String? = gestorRonda
                ?.advertenciaAccionProhibida(posibleAccionProhibida)
                ?.let(context::getString)

            ejecutarAccionProhibida(advertencia, accionProhida)
        }
    }

    private fun ocultarDialogoAccionProhibida() {
        estado.set(EstadoPartida.Estado.InfoAccionProhibida(EstadoAccionProhibida(null, null)))
    }

    private fun ejecutarAccionProhibida(advertencia: String?, accionProhibida: AccionProhibida) {
        advertencia?.let {
            estado.set(EstadoPartida.Estado.InfoAccionProhibida(EstadoAccionProhibida(it, AccionProhibida(
                accionProhibida.posibleAccionProhibida,
                { ejecutarAccionProhibida(null, accionProhibida) },
                {
                    accionProhibida.onNoSePermite()
                    ocultarDialogoAccionProhibida()
                })
            )))
        } ?: run {
            accionProhibida.onSePermite()
            ocultarDialogoAccionProhibida()
        }
    }

    private fun cambiarPaginaActual(pagina: TabData) {
        if (estado.tabActual.value.tab != pagina) {
            onAccionProhibida(
                AccionProhibida(
                    posibleAccionProhibida = PosibleAccionProhibida.CambioTab(pagina),
                    onSePermite = {
                        estado.set(EstadoPartida.Estado.TabActual(pagina))
                        _mostrarPapelera.value = pagina == TabData.JUGADORES
                    },
                    onNoSePermite = {}
                )
            )
        }
    }
    // endregion

    data class InfoRonda(
        val ronda: Ronda,
        val dia: Int,
        val mostrarDialogoSiguienteRonda: Boolean,
        @StringRes val preguntaSiguienteRonda: Int,
        val explicacionVisible: Boolean,
        @StringRes val subtitulo: Int?,
        @StringRes val explicacion: Int,
        val explicacionEsHTMTL: Boolean,
        val mostrarTituloSiguienteRonda: Boolean,
    )
}