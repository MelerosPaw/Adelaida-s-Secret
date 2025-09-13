package com.example.composetest.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetest.data.uc.UC
import com.example.composetest.ui.compose.navegacion.Mensaje
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel: ViewModel() {

    // TODO Melero: 10/9/25 No usar esto para strings!!!!
    @Inject
    @ApplicationContext
    lateinit var context: Context

    private val _mostrarMensaje: MutableLiveData<String?> = MutableLiveData()
    val mostrarMensaje: LiveData<String?> = _mostrarMensaje

    var onMensaje: ((Mensaje) -> Unit)? = null

    protected fun suspender(enMainThread: suspend () -> Unit) {
        viewModelScope.launch {
            enMainThread()
        }
    }

    fun mostrarMensaje(mensaje: Mensaje) {
        viewModelScope.launch(Dispatchers.Main) {
            when {
                onMensaje != null -> onMensaje?.invoke(mensaje)
                _mostrarMensaje.hasObservers() -> {
                    _mostrarMensaje.value = mensaje.mensaje
                    _mostrarMensaje.value = null
                }
                else -> Toast.makeText(context, mensaje.mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    fun <T> UC.Respuesta<T>.usarRespuesta(esValor: (valor: T) -> Unit) {
        onRespuesta(this, esValor)
    }

    fun <T> onRespuesta(respuesta: UC.Respuesta<T>, esValor: (valor: T) -> Unit) {
        when (respuesta) {
            is UC.Respuesta.Valor -> esValor(respuesta.valor)
            is UC.Respuesta.Error -> mostrarMensaje(Mensaje(respuesta.error))
        }
    }
}
