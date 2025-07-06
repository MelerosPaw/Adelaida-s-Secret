package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.composetest.data.uc.AsignarElementoUC
import com.example.composetest.data.uc.ComprarUC
import com.example.composetest.data.uc.GastarCartaUC
import com.example.composetest.data.uc.ReemplazarPistaDeVitrinaUC
import com.example.composetest.data.uc.RobarUC
import com.example.composetest.data.uc.UC
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.ElementoTablero.Carta
import com.example.composetest.model.ElementoTablero.Carta.AcusacionExtra
import com.example.composetest.model.ElementoTablero.Carta.Brandy
import com.example.composetest.model.ElementoTablero.Carta.Llave
import com.example.composetest.model.ElementoTablero.Pista
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.AccionProhibida
import com.example.composetest.ui.compose.PosibleAccionProhibida
import com.example.composetest.ui.compose.dialog.Resultado
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.manager.AsuntoTurbio
import com.example.composetest.ui.manager.AsuntoTurbio.Ninguno
import com.example.composetest.ui.manager.ComprobadorNombreRepetido
import com.example.composetest.ui.manager.GestorCompra
import com.example.composetest.ui.manager.GestorCompra.AccionCompra
import com.example.composetest.ui.manager.GestorPistaPendiente
import com.example.composetest.ui.manager.GestorRobo
import com.example.composetest.ui.viewmodel.TableroViewModel.ElementoClicado
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class TabJugadoresViewModel @Inject constructor() : BaseViewModel() {

    @Inject
    lateinit var gastarCartaUC: Provider<GastarCartaUC>
    @Inject
    lateinit var comprarUC: Provider<ComprarUC>
    @Inject
    lateinit var robarUC: Provider<RobarUC>
    @Inject
    lateinit var reemplazarPistaDeVitrinaUC: Provider<ReemplazarPistaDeVitrinaUC>
    @Inject
    lateinit var asignarElementoUC: Provider<AsignarElementoUC>

    // region Estados
    private val _opcionesClicado: MutableState<OpcionesClicado> = mutableStateOf(OpcionesClicado.OpcionesCasilla())
    val opcionesClicado: State<OpcionesClicado> = _opcionesClicado
    private val _elementoClicado: MutableState<ElementoClicado?> = mutableStateOf(null)
    val elementoClicado: State<ElementoClicado?> = _elementoClicado
    private val _mostrarDialogoCompra: MutableState<AccionCompra?> = mutableStateOf(null)
    val mostrarDialogoCompra: State<AccionCompra?> = _mostrarDialogoCompra
    private val _mostrarDialogoRobo: MutableState<GestorRobo.AccionRobo?> = mutableStateOf(null)
    val mostrarDialogoRobo: State<GestorRobo.AccionRobo?> = _mostrarDialogoRobo
    private val _mostrarDialogoAccionPistaPendiente: MutableState<EstadoPistaPendiente?> = mutableStateOf(null)
    val mostrarDialogoAccionPistaPendiente: State<EstadoPistaPendiente?> = _mostrarDialogoAccionPistaPendiente
    private val _queMostrar: MutableState<MostrarElementos> = mutableStateOf(MostrarElementos.ManoCompleta())
    val queMostrar: State<MostrarElementos> = _queMostrar
    private val _jugadorConCartasAbiertas: MutableState<Jugador?> = mutableStateOf(null)
    val jugadorConCartasAbiertas: State<Jugador?> = _jugadorConCartasAbiertas
    // endregion

    private var idPartida: Long? = null
    var gestorCompra: GestorCompra? = null
    var gestorRobo: GestorRobo? = null
    var gestorPistaPendiente: GestorPistaPendiente? = null
    var jugadores: List<Jugador>? = null
    var onAsuntoTurbioCambiado: ((AsuntoTurbio) -> Unit)? = null
    var onAccionProhibida: ((AccionProhibida) -> Unit)? = null

    // region Públicos
    fun inicializar(
        idPartida: Long?,
        jugadores: List<Jugador>?,
        asuntoTurbio: AsuntoTurbio,
        iniciarAsuntoTurbio: (AsuntoTurbio) -> Unit,
        onAccionProhibida: (AccionProhibida) -> Unit,
    ) {
        this.idPartida = idPartida
        this.jugadores = jugadores
        this.onAsuntoTurbioCambiado = iniciarAsuntoTurbio
        this.onAccionProhibida = onAccionProhibida

        if (asuntoTurbio is Ninguno) {
            finAsuntoTurbio(false)
        }
    }

    fun onCasillaClicada(elementoClicado: ElementoTablero, poseedor: Jugador) {
        ocultarCartas()

        when (_opcionesClicado.value) {
            is OpcionesClicado.OpcionesCasilla -> mostrarDialogoElementoClicado(elementoClicado, poseedor)
            is OpcionesClicado.Compra -> gestorCompra?.run { usarDinero(elementoClicado, comprador, idPartida) }
            is OpcionesClicado.Robo -> gestorRobo?.run { usarBrandy(brandy, elementoClicado, ladron, poseedor, idPartida) }
            is OpcionesClicado.PistaPendiente -> onReemplazarPista(elementoClicado, poseedor)
            is OpcionesClicado.Papelera -> {}
        }
    }

    fun onResultadoDeClicado(resultado: Resultado) {
        cerrarDialogoElementoClicado()

        if (resultado is Resultado.Usar) {
            val carta = resultado.cartaUsada
            when {
                !carta.sePuedeUsar() -> mostrarMensaje(Mensaje("No se pueden usar"))
                carta is AcusacionExtra -> usarAcusacionExtra(carta)
                carta is Brandy -> inicializarRobo(carta, resultado.owner)
                carta is Llave -> usarLlave(carta)
            }
        }
    }

    fun mostrarDialogoElementoClicado(elementoTablero: ElementoTablero, poseedor: Jugador) {
        ocultarCartasSiNoSonDelMismoJugador(poseedor)
        _elementoClicado.value = ElementoClicado(elementoTablero, poseedor)
    }

    fun cerrarDialogoElementoClicado() {
        _elementoClicado.value = null
    }

    fun sePuedeClicar(elemento: ElementoTablero, opcionesClicado: OpcionesClicado): Boolean =
        when (opcionesClicado) {
            is OpcionesClicado.Papelera,
            is OpcionesClicado.Compra,
            is OpcionesClicado.Robo,
            is OpcionesClicado.PistaPendiente -> true
            is OpcionesClicado.OpcionesCasilla -> elemento.sePuedeUsar()
        }

    fun cerrarDialogoDinero() {
        _mostrarDialogoCompra.value = null
    }

    fun comprarPista() {
        gestorCompra?.comprarPista()
    }

    fun comprarSecreto() {
        gestorCompra?.comprarSecreto()
    }

    fun cerrarDialogoBrandy() {
        _mostrarDialogoRobo.value = null
    }

    fun robarDeLaVitrina() {
        gestorRobo?.robarVitrina()
    }

    fun robarDeLaMano() {
        gestorRobo?.robarMano()
    }

    fun iniciarCompra(comprador: Jugador, comprobarSiEstaProhibido: Boolean = true) {
        val onAccionProhibida = onAccionProhibida

        if (comprobarSiEstaProhibido && onAccionProhibida != null) {
            onAccionProhibida(
                AccionProhibida(
                    PosibleAccionProhibida.Compra(),
                    { iniciarCompra(comprador, false) },
                    { /* no-op. Nada que cerrar si no se puede comprar. */ }
                )
            )
        } else {
            ocultarCartas()
            inicializarGestor { jugadores, idPartida ->
                gestorCompra = GestorCompra(
                    idPartida = idPartida,
                    jugadores = jugadores,
                    comprador = comprador,
                    mostrarDialogoDinero = { _mostrarDialogoCompra.value = it },
                    ocultarDialogoDinero = ::cerrarDialogoDinero,
                    ejecutarAsuntoTurbio = { asunto -> cambiarAsuntoTurbio(asunto, true) }
                ).also {
                    it.comprarSicario()
                }
            }
        }
    }

    fun inicializarRobo(brandy: Brandy, ladron: Jugador, comprobarSiEstaProhibido: Boolean = true) {
        val onAccionProhibida = onAccionProhibida

        if (comprobarSiEstaProhibido && onAccionProhibida != null) {
            onAccionProhibida(
                AccionProhibida(
                    PosibleAccionProhibida.Robo(),
                    { inicializarRobo(brandy, ladron, false) },
                    { /* no-op. Nada que cerrar si no se puede robar. */ }
                )
            )
        } else {
            ocultarCartas()
            inicializarGestor { jugadores, idPartida ->
                gestorRobo = GestorRobo(
                    idPartida = idPartida,
                    jugadores = jugadores,
                    ladron = ladron,
                    brandy = brandy,
                    mostrarDialogoBrandy = { _mostrarDialogoRobo.value = it },
                    ocultarDialogoBrandy = ::cerrarDialogoBrandy,
                    ejecutarAsuntoTurbio = { asunto -> cambiarAsuntoTurbio(asunto, true) }
                ).also {
                    it.robar()
                }
            }
        }
    }

    fun onCambiarNombreClicado() {
        ocultarCartas()
    }

    fun comprobarNombreRepetido(nombre: String): Boolean =
        ComprobadorNombreRepetido(jugadores).estaRepetido(nombre)

    fun onMostrarCartas(jugador: Jugador?) {
        _jugadorConCartasAbiertas.value = jugador
    }

    fun onPistaPendienteClicada(pistaPendiente: Pista, poseedor: Jugador) {
        if (gestorPistaPendiente != null) {
            finReemplazo()
        } else {
            idPartida?.let {
                gestorPistaPendiente = GestorPistaPendiente(pistaPendiente, poseedor, it, ::mostrarDialogoPistaPendiente)
                _opcionesClicado.value = OpcionesClicado.PistaPendiente(poseedor)
            }
        }
    }

    fun onCancelarAsignacionPistaPendiente() {
        finReemplazo()
    }

    /**
     *
     * @param pistaADevolver No la usamos porque ya la hemos guardado en el gestor cuando lo
     * inicializamos.
     */
    fun onDevolverPistaPendienteAlTablero(pistaADevolver: Pista, poseedor: Jugador) {
        gestorPistaPendiente?.onMostrarConfirmacion(poseedor,
            GestorPistaPendiente.Accion.DevolucionATablero(::devolverPistaPendienteAlTablero)
        )
    }

    fun ejecutarPistaPendiente(accion: GestorPistaPendiente.Accion) {
        gestorPistaPendiente?.ejecutarAccion(accion)
    }

    private fun mostrarDialogoPistaPendiente(pista: Pista, accion: GestorPistaPendiente.Accion) {
        _mostrarDialogoAccionPistaPendiente.value = EstadoPistaPendiente(pista, accion)
    }

    fun devolverPistaPendienteAlTablero(pistaADevolver: Pista) {
        idPartida?.let {
            suspender {
                val parametros = AsignarElementoUC.Parametros(pistaADevolver, null, it, false, true)
                asignarElementoUC.get().invoke(parametros)
                    .usarRespuesta { finReemplazo() }
            }
        }
    }

    fun cerrarDialogoDevolucion() {
        _mostrarDialogoAccionPistaPendiente.value = null
    }
    // endregion

    // region Privados
    private fun inicializarGestor(
        inicializar: (jugadores: List<Jugador>, idPartida: Long) -> Unit
    ) {
        val jugadores = this.jugadores
        val idPartida = this.idPartida

        when {
            jugadores == null -> mostrarMensaje(Mensaje("No hay jugadores, ¿WTF?"))
            idPartida == null -> mostrarMensaje(Mensaje("No hay partida, ¿WTF?"))
            else -> inicializar(jugadores, idPartida)
        }
    }

    private fun usarLlave(llave: Llave) {
        suspender {
            idPartida?.let {
                gastarCarta(llave, it, "Llave usada")
            }
        }
    }

    private fun usarAcusacionExtra(acusacionExtra: AcusacionExtra) {
        suspender {
            idPartida?.let {
                gastarCarta(acusacionExtra, it, "Acusación extra usada")
            }
        }
    }

    private fun usarDinero(que: ElementoTablero, ladron: Jugador, idPartida: Long) {
        suspender {
            val pista = que as? Pista
            val coste = Pista.PRECIOS[que.prefijo]

            noneNull(pista, coste) { p, c -> comprar(p, c, ladron, idPartida) }
                ?: mostrarMensaje(Mensaje("Dinero o pista nulos."))

            finAsuntoTurbio(true)
        }
    }

    private fun usarBrandy(
        brandy: Brandy,
        cosaARobar: ElementoTablero,
        ladron: Jugador,
        victima: Jugador,
        idPartida: Long
    ) {
        suspender {
            robar(brandy, ladron, victima, cosaARobar, idPartida)
            finAsuntoTurbio(true)
        }
    }

    private suspend fun comprar(
        elemento: Pista,
        coste: Int,
        comprador: Jugador,
        idPartida: Long
    ): Boolean {
        val params = ComprarUC.Parametros(elemento, coste, comprador, idPartida)
        val respuesta = comprarUC.get().invoke(params)
        (respuesta as? UC.Respuesta.Error)?.let { mostrarMensaje(Mensaje(it.error)) }
        return (respuesta as? UC.Respuesta.Valor)?.valor == true
    }

    private suspend fun robar(
        brandy: Brandy,
        ladron: Jugador,
        victima: Jugador,
        cosaRobada: ElementoTablero,
        idPartida: Long
    ): Boolean {
        val params = RobarUC.Parametros(brandy, ladron, victima, cosaRobada, idPartida)
        val respuesta = robarUC.get().invoke(params)
        (respuesta as? UC.Respuesta.Error)?.let { mostrarMensaje(Mensaje(it.error)) }
        return (respuesta as? UC.Respuesta.Valor)?.valor == true
    }

    private fun onReemplazarPista(pistaAReemplazar: ElementoTablero, poseedor: Jugador) {
        (pistaAReemplazar as? Pista)?.let {
            gestorPistaPendiente?.onMostrarConfirmacion(poseedor,
                GestorPistaPendiente.Accion.Reemplazo(it, ::reemplazarPista)
            )
        }
    }

    private fun reemplazarPista(
        pistaAReubicar: Pista,
        pistaADevolverAlTablero: Pista,
        poseedor: Jugador,
        idPartida: Long
    ) {
        suspender {
            val parametros = ReemplazarPistaDeVitrinaUC.Parametros(pistaAReubicar,
                pistaADevolverAlTablero, poseedor, idPartida)
            reemplazarPistaDeVitrinaUC.get().invoke(parametros)
                .usarRespuesta { finReemplazo() }
        }
    }

    private suspend fun gastarCarta(carta: Carta, idPartida: Long, mensajeExito: String) {
        val params = GastarCartaUC.Parametros(carta, idPartida)
        gastarCartaUC.get().invoke(params).usarRespuesta {
            mostrarMensaje(Mensaje(mensajeExito))
        }
    }

    private fun cambiarAsuntoTurbio(asuntoTurbio: AsuntoTurbio, avisar: Boolean) {
        with(asuntoTurbio) {
            _opcionesClicado.value = opcionesClicado
            _queMostrar.value = mostrarElementos

            if (avisar) {
                onAsuntoTurbioCambiado?.let { it(this) }
            }
        }
    }

    private fun finAsuntoTurbio(avisar: Boolean) {
        gestorCompra = null
        cambiarAsuntoTurbio(Ninguno(), avisar)
    }

    private fun ocultarCartas() {
        onMostrarCartas(null)
    }

    private fun ocultarCartasSiNoSonDelMismoJugador(poseedor: Jugador) {
        jugadorConCartasAbiertas.value?.let {
            if (poseedor != it) {
                ocultarCartas()
            }
        }
    }

    private fun finReemplazo() {
        gestorPistaPendiente = null
        _opcionesClicado.value = OpcionesClicado.OpcionesCasilla()
        cerrarDialogoDevolucion()
    }
    // endregion

    //region Clases anidadas
    sealed class OpcionesClicado {
        class OpcionesCasilla() : OpcionesClicado()
        class Compra() : OpcionesClicado()
        class Robo() : OpcionesClicado()
        class Papelera() : OpcionesClicado()
        class PistaPendiente(val jugador: Jugador) : OpcionesClicado()
    }

    class ConfigMarcadorDinero(
        val mostrar: Boolean,
        val sePuedeComprar: Boolean,
        val onDineroClicado: (Jugador) -> Unit = {}
    )

    class EstadoPistaPendiente(val pistaPendiente: Pista, val accion: GestorPistaPendiente.Accion)

    sealed class MostrarElementos(val predicado: (ElementoTablero) -> Boolean) {
        class ManoCompleta(): MostrarElementos({ true })
        class PistasRasgos(val comprador: Jugador): MostrarElementos({ (it as? Pista)?.esDeRasgos() == true })
        class Secretos(val comprador: Jugador): MostrarElementos({ it is Pista && !it.esDeRasgos() })
        class Gastadas(): MostrarElementos({ (it as? Carta)?.estaGastada == true })
        class Vitrina(val ladron: Jugador): MostrarElementos({ it is Pista })
        class Mano(val ladron: Jugador): MostrarElementos({ it is Carta && !it.estaGastada })
    }
    //endregion
}