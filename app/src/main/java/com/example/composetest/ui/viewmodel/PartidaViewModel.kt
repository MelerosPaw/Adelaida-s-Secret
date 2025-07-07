package com.example.composetest.ui.viewmodel

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.composetest.R
import com.example.composetest.data.uc.ActualizarNombrePartidaUC
import com.example.composetest.data.uc.ActualizarRondaUC
import com.example.composetest.data.uc.ObtenerPartidaFlowUC
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
    lateinit var actualizarRondaUC: Provider<ActualizarRondaUC>

    // region Estados
    var estado: EstadoPartida = EstadoPartida()
    private val _idPartida: MutableState<Long?> = mutableStateOf(null)
    val idPartida: State<Long?> = _idPartida
    private val _partida: MutableState<Partida?> = mutableStateOf(null)
    val partida: State<Partida?> = _partida
    private val _infoRonda: MutableState<InfoRonda?> = mutableStateOf(null)
    val infoRonda: State<InfoRonda?> = _infoRonda
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
                }
            }
        }
    }

    // region Públicos
    fun cargarPartida() {
        val params = savedStateHandle.toRoute<PartidaNavegacion>()
        obtenerPartida(params.idPartida)
    }

    fun onCambioRondaSolicitado(infoRondaActual: InfoRonda) {
        _infoRonda.value = infoRondaActual.copy(solicitarCambioRonda = true)
    }

    fun onCondicionesCambioRondaSatisfechas(satisfechas: Boolean) {
        infoRonda.value?.let {
            if (satisfechas) {
                onMostrarDialogoCambioRonda(it)
            } else {
                _infoRonda.value = it.copy(solicitarCambioRonda = false)
            }
        }
    }

    fun onMostrarDialogoCambioRonda(info: InfoRonda) {
        _infoRonda.value = info.copy(
            mostrarDialogoSiguienteRonda = !info.mostrarDialogoSiguienteRonda,
            solicitarCambioRonda = false
        )
    }

    fun onCambiarVisibilidadExplicacionRonda(info: InfoRonda) {
        _infoRonda.value = info.copy(explicacionVisible = !info.explicacionVisible)
    }

    fun onCerrarTituloSiguienteRonda(info: InfoRonda) {
        _infoRonda.value = info.copy(mostrarTituloSiguienteRonda = false, explicacionVisible = true)
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
        partida.value?.let {
            suspender {
                val params = ActualizarRondaUC.Parametros(it)
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
                    .collect { respuesta ->
                        respuesta.usarRespuesta { partida ->
                            _partida.value = partida

                            partida
                                ?.takeIf { it.ronda != infoRonda.value?.ronda }
                                ?.let {
                                    if (gestorRonda == null || gestorRonda?.esOtraRonda(it.ronda) == true) {
                                        gestorRonda = GestorRonda.Factory.from(it.ronda) { mensaje ->
                                            consumidor.consumir(IntencionPartida.MostrarMensaje(mensaje))
                                        }
                                    }
                                    estado.setPartida(it)
                                    initInfoRonda(it)
                                }
                        }
                    }
            }
        }
    }

    private fun initInfoRonda(partida: Partida) {
        _infoRonda.value = partida.ronda.let {
            InfoRonda(
                it,
                partida.dia,
                false,
                getPreguntaSiguienteRonda(it),
                false,
                getSubtitulo(it),
                getExplicacion(it),
                getExplicacionEsHtml(it),
                true,
                false
            )
        }
    }

    @StringRes
    fun getSubtitulo(ronda: Ronda): Int? = when(ronda) {
        Ronda.MEDIODIA -> R.string.alias_ronda_mediodia
        Ronda.TARDE -> R.string.alias_ronda_tarde
        Ronda.NOCHE, Ronda.NO_VALIDO, Ronda.MANANA -> null
    }

    @StringRes
    fun getExplicacion(ronda: Ronda): Int = when(ronda) {
        Ronda.MANANA -> R.string.explicacion_manana
        Ronda.MEDIODIA -> R.string.explicacion_mediodia
        Ronda.TARDE -> R.string.explicacion_tarde
        Ronda.NOCHE -> R.string.explicacion_primera_noche
        Ronda.NO_VALIDO -> R.string.no_valido
    }

    fun getExplicacionEsHtml(ronda: Ronda): Boolean = when(ronda) {
        Ronda.MANANA -> false
        Ronda.MEDIODIA -> false
        Ronda.TARDE -> true
        Ronda.NOCHE -> false
        Ronda.NO_VALIDO -> false
    }

    @StringRes
    fun getPreguntaSiguienteRonda(ronda: Ronda): Int = when(ronda) {
        Ronda.MANANA -> R.string.pregunta_fin_manana
        Ronda.MEDIODIA -> R.string.pregunta_fin_mediodia
        Ronda.TARDE -> R.string.pregunta_fin_tarde
        Ronda.NOCHE -> R.string.pregunta_fin_noche
        Ronda.NO_VALIDO -> R.string.no_valido
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
        val solicitarCambioRonda: Boolean,
    )
}