package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.Casilla
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.ElementoTablero.Carta
import com.example.composetest.model.ElementoTablero.Carta.Prefijo.ACUSACION_EXTRA
import com.example.composetest.model.ElementoTablero.Carta.Prefijo.BRANDY
import com.example.composetest.model.ElementoTablero.Carta.Prefijo.DINERO
import com.example.composetest.model.ElementoTablero.Carta.Prefijo.LLAVE
import com.example.composetest.model.ElementoTablero.Carta.Prefijo.PERSESKUD
import com.example.composetest.model.ElementoTablero.Pista
import com.example.composetest.model.ElementoTablero.Pista.Prefijo.COARTADA
import com.example.composetest.model.ElementoTablero.Pista.Prefijo.HABITO
import com.example.composetest.model.ElementoTablero.Pista.Prefijo.OBJETO
import com.example.composetest.model.ElementoTablero.Pista.Prefijo.SECRETO
import com.example.composetest.model.ElementoTablero.Pista.Prefijo.TESTIGO
import com.example.composetest.model.ElementoTablero.Prefijo
import com.example.composetest.model.Habitacion
import com.example.composetest.model.Jugador
import com.example.composetest.model.Tablero
import com.example.composetest.model.aCaracter
import com.example.composetest.ui.CasillaVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TableroViewModel @Inject constructor() : BaseViewModel() {

    private val _impresoraState: MutableState<Tablero.Impresora?> = mutableStateOf(null)
    val impresoraState: State<Tablero.Impresora?> = _impresoraState
    private val _mostrarEsquemaState: MutableState<Boolean> = mutableStateOf(false)
    val mostrarEsquemaState: State<Boolean> = _mostrarEsquemaState
    private val _estaCargando: MutableState<Boolean> = mutableStateOf(false)
    val estaCargando: State<Boolean> = _estaCargando
    private val _elementoClicado: MutableState<ElementoClicado?> = mutableStateOf(null)
    val elementoClicado: State<ElementoClicado?> = _elementoClicado

    private var impresora: Tablero.Impresora? = null
    var puedeClicarCasillas: Boolean = false
    var elementosFueraDelTablero: List<ElementoTablero> = emptyList()
    var idPartida: Long? = null
    var jugadores: List<Jugador>? = null

    fun inicializar(
        idPartida: Long?,
        tablero: Tablero,
        elementosFueraDelTablero: List<ElementoTablero>,
        puedeClicarCasilla: Boolean,
        jugadores: List<Jugador>?
    ) {
        this.idPartida = idPartida
        this.puedeClicarCasillas = puedeClicarCasilla
        this.elementosFueraDelTablero = elementosFueraDelTablero
        this.jugadores = jugadores

        if (impresora == null) {
            impresora = Tablero.Impresora(tablero)
            _impresoraState.value = impresora
        } else {
            actualizarImpresora {
                this.tablero = tablero
            }
        }
    }

    fun getOwner(jugadores: List<Jugador>, elemento: ElementoTablero): Jugador? =
        jugadores.find { it.mano().contains(elemento) }

    fun getCasillasEnFila(tablero: Tablero, fila: Int): Array<CasillaVO> {
        val casillasEnLaFila = tablero.casillas.filter { it.getFilaComoNumero() == fila }
        val idFila = fila.aCaracter()

        return Array(tablero.cantidadColumnas) { columna ->
            val casilla = casillasEnLaFila.firstOrNull { casilla -> casilla.columna == columna + 1 }
                ?: Casilla(idFila, columna + 1)
            val estaEnElTablero = casilla.contenido?.let { it in elementosFueraDelTablero } != true
            val estaFiltrado = noneNull(casilla.contenido, impresora) { contenido, impresora ->
                !impresora.noEstaFiltrado(contenido)
            } == true
            val puedeMostrarse = !estaFiltrado && (estaEnElTablero || casilla.contenido is Pista)
            val puedeDevolverseAlTablero = !estaEnElTablero && puedeMostrarse

            CasillaVO(casilla, estaEnElTablero, puedeMostrarse, puedeDevolverseAlTablero)
        }
    }

    fun getColorDeCasilla(casilla: Casilla): Color = impresora?.tablero?.habitaciones?.firstOrNull {
            it.contieneCasilla(casilla)
        }?.colorHabitacion() ?: Color.Transparent

    fun onFiltrosCambiados(filtros: FiltrosViewModel.EstadoFiltros) {
        actualizarImpresora {
            usarIconos = filtros.usarIconos
            usarValores = filtros.mostrarValores
            mostrarIdHabitacionSiLaCeldaQuedaVacia =
                filtros.mostrarValorHabitacionSiLaCeldaQuedaVacia
            _mostrarEsquemaState.value = filtros.mostrarEsquema
            
            ponerFiltro(filtros, HABITO, Pista.Habito(1))
            ponerFiltro(filtros, OBJETO, Pista.Objeto(1))
            ponerFiltro(filtros, TESTIGO, Pista.Testigo(1))
            ponerFiltro(filtros, COARTADA, Pista.Coartada(1))
            ponerFiltro(filtros, SECRETO, Pista.Secreto(1))
            ponerFiltro(filtros, BRANDY, Carta.Brandy(1))
            ponerFiltro(filtros, DINERO, Carta.Dinero(1, 100))
            ponerFiltro(filtros, ACUSACION_EXTRA, Carta.AcusacionExtra())
            ponerFiltro(filtros, PERSESKUD, Carta.Perseskud())
            ponerFiltro(filtros, LLAVE, Carta.Llave(1))
        }
    }

    fun onCasillaClicada(casilla: Casilla) {
        suspender {
            withContext(Dispatchers.Default) {
                val elemento = casilla.contenido
                _elementoClicado.value = noneNull(jugadores, elemento) { jug, elm ->
                    val poseedor = getOwner(jug, elm)
                    ElementoClicado(elm, poseedor)
                }
            }
        }
    }

    fun cerrarDialogCasillaClicada() {
        _elementoClicado.value = null
    }

    fun puedeMostrarseEnElTablero(contenido: ElementoTablero, casilla: CasillaVO): Boolean =
        impresora?.noEstaFiltrado(contenido) == true
                && (casilla.contenidoEstaEnElTablero || contenido is Pista)

    private fun Tablero.Impresora.ponerFiltro(
        filtros: FiltrosViewModel.EstadoFiltros,
        prefijo: Prefijo,
        elemento: ElementoTablero
    ) {
        if (filtros.elementos.contains(prefijo)) {
            nuevoFiltro(elemento)
        } else {
            borrarFiltro(elemento)
        }
    }

    private fun actualizarImpresora(actualizacion: Tablero.Impresora.() -> Unit) {
        impresora?.run {
            _impresoraState.value = copiar()
                .apply(actualizacion)
                .also { impresora = it }
        }
    }

    private fun Habitacion.colorHabitacion(): Color = when (this) {
        is Habitacion.Salon -> Color(0xFF78B3BB)
        is Habitacion.GabineteEsoterico -> Color(0xDD8F4EA5)
        is Habitacion.Dormitorio -> Color(0xFFC99179)
        is Habitacion.Pasillo -> Color(0xFF66503F)
        is Habitacion.AntesalaDormitorio -> Color(0xFF717B40)
        is Habitacion.Despacho -> Color(0xFFAE4A4A)
        is Habitacion.SalaCuadros -> Color(0xFFC2C2C2)
        is Habitacion.CuartoCostura -> Color(0xFF9C6F84)
        else -> Color.White
    }

    class ElementoClicado(val elemento: ElementoTablero, val poseedor: Jugador?)
}