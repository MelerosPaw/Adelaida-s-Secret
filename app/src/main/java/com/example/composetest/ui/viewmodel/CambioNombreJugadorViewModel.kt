package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.composetest.data.uc.CambiarNombreJugadorUC
import com.example.composetest.model.Jugador
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class CambioNombreJugadorViewModel @Inject constructor(
    private val cambiarNombreJugadorUC: Provider<CambiarNombreJugadorUC>
) : BaseViewModel() {

    private var idPartida: Long? = null
    private var onComprobarNombreRepetido: ((String) -> Boolean)? = null

    private val _estadoDialogo: MutableState<EstadoCambioNombre?> = mutableStateOf(null)
    val estadoDialogo: State<EstadoCambioNombre?> = _estadoDialogo

    fun mostrarDialogo(jugador: Jugador) {
        _estadoDialogo.value = EstadoCambioNombre(jugador)
    }

    fun inicializar(idPartida: Long?, onComprobarNombreRepetido: ((String) -> Boolean)?) {
        this.idPartida = idPartida
        this.onComprobarNombreRepetido = onComprobarNombreRepetido
    }

    fun onCambioNombreEnDialogo(estado: EstadoCambioNombre, nombre: String) {
        _estadoDialogo.value = estado.copy(nombre = nombre, error = "")
    }

    fun cerrarDialogo() {
        _estadoDialogo.value = null
    }

    fun cambiarNombre(estado: EstadoCambioNombre) {
        val nombreFinal = estado.nombre.trim()

        when {
            nombreFinal.isEmpty() ->
                _estadoDialogo.value = estado.copy(error = "El nombre no puede estar vacío")

            onComprobarNombreRepetido?.invoke(nombreFinal) == true ->
                _estadoDialogo.value =
                    estado.copy(error = "Ya hay un jugador con este mismo nombre.")

            else -> idPartida?.let {
                suspender {
                    val params = CambiarNombreJugadorUC.Parametros(estado.jugador, it, nombreFinal)
                    cambiarNombreJugadorUC.get()(params)
                    cerrarDialogo()
                }
            }
        }
    }

    data class EstadoCambioNombre(
        val jugador: Jugador,
        val nombre: String = jugador.nombre,
        val error: String = ""
    )
}