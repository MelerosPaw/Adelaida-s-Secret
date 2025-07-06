package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.composetest.data.uc.AsignarJugadoresYNombrePartida
import com.example.composetest.data.uc.UC
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.navegacion.SeleccionJugadores
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class SeleccionJugadoresViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    @Inject
    lateinit var asignarJugadoresYNombreUC: AsignarJugadoresYNombrePartida

    private val _listadoJugadores: MutableState<List<String>> = mutableStateOf(emptyList())
    val listadoJugadores: State<List<String>> = _listadoJugadores
    private val _nombrePartida: MutableState<String> = mutableStateOf(savedStateHandle.toRoute<SeleccionJugadores>().nombrePartida)
    val nombrePartida: State<String> = _nombrePartida
    private val _puedeIniciarPartida: MutableState<Boolean> = mutableStateOf(false)
    val puedeIniciarPartida: State<Boolean> = _puedeIniciarPartida
    private val _mostrarDialogoComenzar: MutableState<Boolean> = mutableStateOf(false)
    val mostrarDialogoComenzar: State<Boolean> = _mostrarDialogoComenzar
    private val _nuevoJugador: MutableState<EstadoNombreJugador> = mutableStateOf(EstadoNombreJugador(""))
    val nuevoJugador: State<EstadoNombreJugador> = _nuevoJugador
    private val _errorJugadorRepetido: MutableState<String?> = mutableStateOf(null)
    val errorJugadorRepetido: State<String?> = _errorJugadorRepetido

    private val idPartida: Long = savedStateHandle.toRoute<SeleccionJugadores>().idPartida
    private val jugadores: MutableList<String> = LinkedList()

    fun actualizarNuevoJugador(nombre: String) {
        _nuevoJugador.value = EstadoNombreJugador(nombre)

        if (nombre.trim() in jugadores) {
            _errorJugadorRepetido.value = "Nombre repetido"
        } else {
            _errorJugadorRepetido.value = null
        }
    }

    fun alCambiarNombrePartida(nombrePartida: String) {
        _nombrePartida.value = nombrePartida
    }

    fun guardarJugador(nombre: String) {
        nombre.trim().takeIf(String::isNotBlank)?.let {
            jugadores.add(it)
            actualizarNuevoJugador("")
            actualizarPuedeIniciarPartida()
            listarJugadores()
        }
    }

    fun eliminarJugador(nombre: String) {
        jugadores.remove(nombre)
        listarJugadores()
        actualizarPuedeIniciarPartida()
        actualizarNuevoJugador(_nuevoJugador.value.nombre)
    }

    fun listarJugadores() {
        _listadoJugadores.value = LinkedList(jugadores)
    }

    fun mostrarDialogoComenzar() {
        _mostrarDialogoComenzar.value = true
    }

    fun ocultarDialogoComenzar() {
        _mostrarDialogoComenzar.value = false
    }

    fun guardarJugadores(onNavegarAlSiguientePaso: (partida: Partida) -> Unit) {
        suspender {
            _nombrePartida.value?.let { nombre ->
                val parametros = AsignarJugadoresYNombrePartida.Parametros(jugadores, nombre, idPartida)
                val respuesta = asignarJugadoresYNombreUC(parametros)
                when (respuesta) {
                    is UC.Respuesta.Valor -> onJugadoresSeleccionados(respuesta.valor, onNavegarAlSiguientePaso)
                    is UC.Respuesta.Error -> mostrarErrorComienzoPartida(respuesta.error)
                }
            }
        }
    }

    private fun onJugadoresSeleccionados(
        partida: Partida,
        onNavegarAlSiguientePaso: (Partida) -> Unit
    ) {
        ocultarDialogoComenzar()
        onNavegarAlSiguientePaso(partida)
    }

    private fun mostrarErrorComienzoPartida(error: String) {
        mostrarMensaje(Mensaje(error))
        ocultarDialogoComenzar()
    }

    private fun actualizarPuedeIniciarPartida() {
        _puedeIniciarPartida.value = esNumeroSuficienteDeJugadores()
    }

    private fun esNumeroSuficienteDeJugadores() = jugadores.size > 1

    class EstadoNombreJugador(val nombre: String)
}