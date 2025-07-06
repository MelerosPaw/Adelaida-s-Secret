package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.composetest.data.uc.PartidaRepetidaUC
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class DialogoNombrePartidaViewModel @Inject constructor() : BaseViewModel() {

    @Inject
    lateinit var partidaRepetidaUC: Provider<PartidaRepetidaUC>

    private val _textoEnCampo: MutableState<String> = mutableStateOf("")
    val textoEnCampo: State<String> = _textoEnCampo
    private val _error: MutableState<String> = mutableStateOf("")
    val error: State<String> = _error

    var isDebug: Boolean = false

    fun inicializar(nombre: String?, isDebug: Boolean) {
        if (textoEnCampo.value.isBlank()) {
            this.isDebug = isDebug
            _textoEnCampo.value = nombre?.takeIf { !it.esUnPlaceholder() }.orEmpty()
        }
    }

    fun onCampoCambiado(textoEnCampo: String) {
        _textoEnCampo.value = textoEnCampo
        quitarError()
    }

    fun cambiarNombrePartida(
        nuevoNombre: String,
        nombreAnterior: String?,
        onMismoNombre: () -> Unit,
        onNombrePartidaChanged: (String) -> Unit
    ) {

        if (nuevoNombre.isBlank()) {
            _error.value = "El nombre no puede estar vacío"

        } else if (nuevoNombre == nombreAnterior) {
            quitarError()
            onMismoNombre()

        } else {
            suspender {
                val estaRepetido = partidaRepetidaUC.get()
                    .invoke(PartidaRepetidaUC.Parametros(nuevoNombre))

                if (estaRepetido) {
                    _error.value = "Ya hay una partida con este nombre"

                } else {
                    quitarError()
                    onNombrePartidaChanged.invoke(nuevoNombre)
                }
            }
        }
    }

    private fun quitarError() {
        _error.value = ""
    }

    private fun String.esUnPlaceholder(): Boolean = this in listOf(JUEGO_NUEVO, DEBUG_MODE)
}