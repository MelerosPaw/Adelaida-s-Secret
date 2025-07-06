package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.composetest.data.uc.LeerPistaUC
import com.example.composetest.model.ElementoTablero
import com.example.composetest.ui.compose.AccionProhibida
import com.example.composetest.ui.compose.PosibleAccionProhibida
import com.example.composetest.ui.compose.TabData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class DialogoElementoClicadoViewModel @Inject constructor(): BaseViewModel() {

    @Inject
    lateinit var leerPistaUC: Provider<LeerPistaUC>

    private val _dialogoSeleccionJugadores: MutableState<Boolean> = mutableStateOf(false)
    var dialogoSeleccionJugadores: State<Boolean> = _dialogoSeleccionJugadores
    private val _dialogoLeerPista: MutableState<EstadoLeerPista?> = mutableStateOf(null)
    var dialogoLeerPista: State<EstadoLeerPista?> = _dialogoLeerPista

    var onAccionProhibida: ((AccionProhibida) -> Unit)? = null

    fun abrirDialogoSeleccionJugadores(elemento: ElementoTablero, comprobar: Boolean = true) {
        val onAccionProhibida = onAccionProhibida

        if (comprobar && onAccionProhibida != null) {
            // Aquí no se conoce el tab, hay que pasar cualquiera, que ya lo meterá el ViewModel que recibe la accion
            val accionProhibida = AccionProhibida(PosibleAccionProhibida.Reasignacion(elemento, TabData.INFO),
                { abrirDialogoSeleccionJugadores(elemento, false) },
                { }
            )
            onAccionProhibida(accionProhibida)
        } else {
            _dialogoSeleccionJugadores.value = true
        }
    }

    fun ocultarDialogoSeleccionJugadores() {
        _dialogoSeleccionJugadores.value = false
    }

    fun leerPista(pista: ElementoTablero.Pista) {
        suspender {
            leerPistaUC.get().invoke(LeerPistaUC.Parametros(pista.id))
                .usarRespuesta { mostrarDialogoLeerPista(EstadoLeerPista(pista, it.texto)) }
        }
    }

    private fun mostrarDialogoLeerPista(estado: EstadoLeerPista) {
        _dialogoLeerPista.value = estado
    }

    fun ocultarDialogoLeerPista() {
        _dialogoLeerPista.value = null
    }

    class EstadoLeerPista(
        val pista: ElementoTablero.Pista,
        val texto: String
    )
}