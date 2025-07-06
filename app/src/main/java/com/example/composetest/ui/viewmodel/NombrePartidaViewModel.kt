package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.composetest.data.uc.CrearPartidaUC
import com.example.composetest.model.Partida
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val JUEGO_NUEVO = "Juego nuevo"
const val DEBUG_MODE = "Debug mode"

@HiltViewModel
class NombrePartidaViewModel @Inject constructor() : BaseViewModel() {

    @Inject
    lateinit var crearPartidaUC: CrearPartidaUC

    private val _nombreEnElTitulo: MutableState<String> = mutableStateOf("<Sin nombre>")
    val nombreEnElTitulo: State<String> = _nombreEnElTitulo
    private val _mostrarDialogoCambioNombre: MutableState<Boolean> = mutableStateOf(false)
    val mostrarDialogoCambioNombre: State<Boolean> = _mostrarDialogoCambioNombre

    fun setNombre(nombreOriginal: String?, isDebug: Boolean) {
        _nombreEnElTitulo.value = nombreOriginal ?: JUEGO_NUEVO.takeIf { !isDebug } ?: DEBUG_MODE
    }

    fun onNombreCambiado(nombre: String) {
        _nombreEnElTitulo.value = nombre
        ocultarDialogoCambioNombre()
    }

    fun mostrarDialogoCambioNombre() {
        _mostrarDialogoCambioNombre.value = true
    }

    fun cancelarCambioNombre() {
        ocultarDialogoCambioNombre()
    }

    fun crearPartida(nombre: String, navegarASiguientePaso: (partida: Partida) -> Unit) {
        suspender {
            crearPartidaUC(CrearPartidaUC.Parametros(nombre))
                .usarRespuesta { navegarASiguientePaso(it) }
        }
    }

    private fun ocultarDialogoCambioNombre() {
        _mostrarDialogoCambioNombre.value = false
    }
}