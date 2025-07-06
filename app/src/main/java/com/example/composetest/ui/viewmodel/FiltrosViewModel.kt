package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.composetest.model.ElementoTablero
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FiltrosViewModel @Inject constructor() : BaseViewModel() {

    //region Fields
    private val _estadoFiltros: MutableState<EstadoFiltros> = mutableStateOf(EstadoFiltros())
    val estadoFiltros: MutableState<EstadoFiltros> = _estadoFiltros
    private val _puedeResetearFiltros: MutableState<Boolean> = mutableStateOf(false)
    val puedeResetearFiltros: State<Boolean> = _puedeResetearFiltros

    private var filtroHabitos: MutableState<Boolean> = mutableStateOf(false)
    private var filtroObjetos: MutableState<Boolean> = mutableStateOf(false)
    private var filtroCoartadas: MutableState<Boolean> = mutableStateOf(false)
    private var filtroTestigos: MutableState<Boolean> = mutableStateOf(false)
    private var filtroSecretos: MutableState<Boolean> = mutableStateOf(false)
    private var filtroBrandy: MutableState<Boolean> = mutableStateOf(false)
    private var filtroDinero: MutableState<Boolean> = mutableStateOf(false)
    private var filtroLlaves: MutableState<Boolean> = mutableStateOf(false)
    private var filtroAcusacionExtra: MutableState<Boolean> = mutableStateOf(false)
    private var filtroPerseskud: MutableState<Boolean> = mutableStateOf(false)

    val filtrosDePistas: Array<FiltroWrapper> = arrayOf(
        FiltroWrapper("Hábitos", filtroHabitos, ::mostrarHabitos),
        FiltroWrapper("Objetos", filtroObjetos, ::mostrarObjetos),
        FiltroWrapper("Testigo", filtroTestigos, ::mostrarTestigos),
        FiltroWrapper("Coartada", filtroCoartadas, ::mostrarCoartada),
        FiltroWrapper("Secretos", filtroSecretos, ::mostrarSecretos),
    )

    val filtrosDeCartas: Array<FiltroWrapper> = arrayOf(
        FiltroWrapper("Brandy", filtroBrandy, ::mostrarBrandy),
        FiltroWrapper("Dinero", filtroDinero, ::mostrarDinero),
        FiltroWrapper("Llaves", filtroLlaves, ::mostrarLlaves),
        FiltroWrapper("Acusación extra", filtroAcusacionExtra, ::mostrarAcusacionExtra),
        FiltroWrapper("Perseskud", filtroPerseskud, ::mostrarPerseskud),
    )

    var onEstadoCambiado: (EstadoFiltros) -> Unit = {}
    //endregion

    //region Public methods
    fun conIconos(usarIconos: Boolean) {
        actualizarEstado {
            it.copy(usarIconos = usarIconos)
        }
    }

    fun conValores(mostrarValores: Boolean) {
        actualizarEstado {
            it.copy(mostrarValores = mostrarValores)
        }
    }

    fun habitacionesVacias(rellenarHabitacionesVacias: Boolean) {
        actualizarEstado {
            it.copy(mostrarValorHabitacionSiLaCeldaQuedaVacia = rellenarHabitacionesVacias)
        }
    }

    fun mostrarEsquema(mostrar: Boolean) {
        actualizarEstado { it.copy(mostrarEsquema = mostrar) }
    }

    fun resetearFiltros() {
        filtroHabitos.value = false
        filtroObjetos.value = false
        filtroCoartadas.value = false
        filtroTestigos.value = false
        filtroSecretos.value = false
        filtroBrandy.value = false
        filtroDinero.value = false
        filtroLlaves.value = false
        filtroAcusacionExtra.value = false
        filtroPerseskud.value = false
        _puedeResetearFiltros.value = false

        actualizarEstado {
            it.limpiarFiltros()
            it.copy()
        }
    }
    //endregion

    //region Private methods
    private fun mostrarHabitos(mostrar: Boolean) {
        ponerFiltro(ElementoTablero.Pista.Prefijo.HABITO, filtroHabitos, mostrar)
    }

    private fun mostrarObjetos(mostrar: Boolean) {
        ponerFiltro(ElementoTablero.Pista.Prefijo.OBJETO, filtroObjetos, mostrar)
    }

    private fun mostrarTestigos(mostrar: Boolean) {
        ponerFiltro(ElementoTablero.Pista.Prefijo.TESTIGO, filtroTestigos, mostrar)
    }

    private fun mostrarCoartada(mostrar: Boolean) {
        ponerFiltro(ElementoTablero.Pista.Prefijo.COARTADA, filtroCoartadas, mostrar)
    }

    private fun mostrarSecretos(mostrar: Boolean) {
        ponerFiltro(ElementoTablero.Pista.Prefijo.SECRETO, filtroSecretos, mostrar)
    }

    private fun mostrarBrandy(mostrar: Boolean) {
        ponerFiltro(ElementoTablero.Carta.Prefijo.BRANDY, filtroBrandy, mostrar)
    }

    private fun mostrarDinero(mostrar: Boolean) {
        ponerFiltro(ElementoTablero.Carta.Prefijo.DINERO, filtroDinero, mostrar)
    }

    private fun mostrarAcusacionExtra(mostrar: Boolean) {
        ponerFiltro(ElementoTablero.Carta.Prefijo.ACUSACION_EXTRA, filtroAcusacionExtra, mostrar)
    }

    private fun mostrarPerseskud(mostrar: Boolean) {
        ponerFiltro(ElementoTablero.Carta.Prefijo.PERSESKUD, filtroPerseskud, mostrar)
    }

    private fun mostrarLlaves(mostrar: Boolean) {
        ponerFiltro(ElementoTablero.Carta.Prefijo.LLAVE, filtroLlaves, mostrar)
    }

    private fun ponerFiltro(
        filtro: ElementoTablero.Prefijo,
        estado: MutableState<Boolean>,
        activo: Boolean,
    ) {
        actualizarEstado {
            it.ponerFiltro(filtro, activo)
            estado.value = activo
            _puedeResetearFiltros.value = it.tieneFiltros()
            it.copy(elementos = it.elementos)
        }
    }

    private fun actualizarEstado(actualizar: (EstadoFiltros) -> EstadoFiltros) {
        _estadoFiltros.value = actualizar(_estadoFiltros.value)
        onEstadoCambiado(_estadoFiltros.value)
    }
    //endregion

    //region Inner classes
    /**
     * Define el nombre, el estado de Compose y el método que lo modifica para generar vistas de
     * filtros dinámicamente.
     */
    class FiltroWrapper(
        val nombre: String,
        val estado: State<Boolean>,
        val modificacion: (Boolean) -> Unit
    )

    data class EstadoFiltros(
        val usarIconos: Boolean = false,
        val mostrarValores: Boolean = true,
        val mostrarValorHabitacionSiLaCeldaQuedaVacia: Boolean = false,
        val mostrarEsquema: Boolean = false,
        val elementos: MutableSet<ElementoTablero.Prefijo> = mutableSetOf(),
    ) {

        fun ponerFiltro(prefijo: ElementoTablero.Prefijo, poner: Boolean) {
            if (poner) {
                elementos.add(prefijo)
            } else {
                elementos.remove(prefijo)
            }
        }

        fun limpiarFiltros() {
            elementos.clear()
        }

        fun tieneFiltros(): Boolean = elementos.isNotEmpty()
    }
    //endregion
}