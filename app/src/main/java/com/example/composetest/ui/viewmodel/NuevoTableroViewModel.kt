package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.composetest.data.uc.ActualizarEstadoCreacion
import com.example.composetest.data.uc.CargarTableroUC
import com.example.composetest.data.uc.CrearTableroUC
import com.example.composetest.data.uc.GuardarTableroUC
import com.example.composetest.data.uc.UC.Respuesta.Error
import com.example.composetest.data.uc.UC.Respuesta.Valor
import com.example.composetest.model.Partida
import com.example.composetest.model.Sospechoso
import com.example.composetest.model.Tablero
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.navegacion.NuevoTablero
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NuevoTableroViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    @Inject
    lateinit var crearTableroUC: CrearTableroUC

    @Inject
    lateinit var guardarTableroUC: GuardarTableroUC

    @Inject
    lateinit var cargarTableroUC: CargarTableroUC

    @Inject
    lateinit var actualizarEstadoCreacion: ActualizarEstadoCreacion

    private val _puedeComenzar: MutableState<Boolean> = mutableStateOf(false)
    val puedeComenzar: State<Boolean> = _puedeComenzar
    private val _mostrarDialogoComenzar: MutableState<Boolean> = mutableStateOf(false)
    val mostrarDialogoComenzar: State<Boolean> = _mostrarDialogoComenzar
    private val filtrosAbiertos: MutableState<Boolean> = mutableStateOf(false)
    val estadoFiltros: State<Boolean> = filtrosAbiertos
    private val _estaCargando: MutableState<Boolean> = mutableStateOf(false)
    val estaCargando: State<Boolean> = _estaCargando
    private val _tablero: MutableState<Tablero?> = mutableStateOf(null)
    val tablero: State<Tablero?> = _tablero
    private val _asesino: MutableState<Sospechoso?> = mutableStateOf(null)
    val asesino: State<Sospechoso?> = _asesino

    private var partida: Partida? = null
    var idPartida: Long? = null

    fun noTieneTablero(): Boolean = _tablero.value == null && _estaCargando.value == false

    fun cargarTablero() {
        savedStateHandle.toRoute<NuevoTablero>().idPartida?.let { id ->
            this.idPartida = id

            suspender {
                _estaCargando.value = true
                val respuesta = cargarTableroUC(CargarTableroUC.Parametros(id))

                when (respuesta) {
                    is Valor -> onTableroCargado(respuesta)
                    is Error -> onErrorAlCargarTablero(respuesta)
                }
            }
        }
    }

    fun nuevoTablero() {
        suspender {
            _estaCargando.value = true
            val tablero = crearTablero()
            tablero?.let {
                val guardado = guardarTablero(tablero)
                _tablero.value = tablero.takeIf { guardado || idPartida == null }
                _puedeComenzar.value = idPartida != null
            }

            _estaCargando.value = false
        }
    }

    private suspend fun crearTablero(): Tablero? {
        val parametros = CrearTableroUC.Parametros(partida)
        val respuesta = crearTableroUC(parametros)

        when (respuesta) {
            is Valor -> respuesta.valor
            is Error -> mostrarMensaje(Mensaje(respuesta.error))
        }

        return (respuesta as? Valor)?.valor
    }

    fun abrirFiltros() {
        filtrosAbiertos.value = true
    }

    fun cerrarFiltros() {
        filtrosAbiertos.value = false
    }

    fun mostrarConfirmacionInicio() {
        _mostrarDialogoComenzar.value = true
    }

    fun ocultarConfirmacionInicio() {
        _mostrarDialogoComenzar.value = false
    }

    suspend fun guardarTablero(tablero: Tablero): Boolean = idPartida?.let {
        val parametros = GuardarTableroUC.Parametros(it, tablero)
        val resultado = guardarTableroUC(parametros)

        when (resultado) {
            is Valor -> onTableroGuardado(resultado.valor)
            is Error -> onErrorAlGuardarPartida(resultado)
        }

        resultado is Valor
    } == true

    fun guardarTableroYComenzar(onNavegarASiguientePaso: (partida: Partida) -> Unit) {
        partida?.let{
            suspender {
                _estaCargando.value = true
                val respuesta = actualizarEstadoCreacion(ActualizarEstadoCreacion.Parametros.ConPartida(it))

                when (respuesta) {
                    is Valor -> this.comenzarPartida(respuesta.valor, onNavegarASiguientePaso)
                    is Error -> {
                        ocultarConfirmacionInicio()
                        mostrarMensaje(Mensaje(respuesta.error))
                    }
                }
            }
        }
    }

    private fun onTableroCargado(respuesta: Valor<CargarTableroUC.PartidaYTablero>) {
        partida = respuesta.valor.partida
        _asesino.value = partida?.asesino
        _tablero.value = respuesta.valor.tablero?.also {
            _puedeComenzar.value = true
        }
        _estaCargando.value = false
    }

    private fun onErrorAlCargarTablero(respuesta: Error<CargarTableroUC.PartidaYTablero>) {
        mostrarMensaje(Mensaje(respuesta.error))
        _estaCargando.value = false
        _puedeComenzar.value = false
    }

    private fun onTableroGuardado(partida: Partida) {
        this.partida = partida
    }

    private fun onErrorAlGuardarPartida(resultado: Error<Partida>) {
        mostrarMensaje(Mensaje(resultado.error))
    }

    private fun comenzarPartida(
        partida: Partida,
        onNavegarASiguientePaso: (partida: Partida) -> Unit
    ) {
        ocultarConfirmacionInicio()
        onNavegarASiguientePaso(partida)
    }
}