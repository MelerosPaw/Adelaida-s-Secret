package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.composetest.data.uc.BorrarPartidaCompletaUC
import com.example.composetest.data.uc.None
import com.example.composetest.data.uc.ObtenerPartidasUC
import com.example.composetest.data.uc.UC
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.viewmodel.CargarPartidaViewModel.EstadoCargarEliminar.Finalidad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CargarPartidaViewModel @Inject constructor() : BaseViewModel() {

    @Inject
    lateinit var obtenerPartidasUC: ObtenerPartidasUC
    @Inject
    lateinit var borrarPartidaCompletaUC: BorrarPartidaCompletaUC

    private val _listadoPartidas: MutableState<Flow<List<Partida>?>?> = mutableStateOf(null)
    val listadoPartidas: State<Flow<List<Partida>?>?> = _listadoPartidas
    private val _mostrarCargarEliminar: MutableState<EstadoCargarEliminar> =
        mutableStateOf(EstadoCargarEliminar())
    val mostrarCargarEliminar: State<EstadoCargarEliminar> = _mostrarCargarEliminar

    fun cargarPartidas() {
        suspender {
            _listadoPartidas.value = obtenerPartidasUC(None)
        }
    }

    fun continuarPartida(
        partida: Partida,
        onNavegarASiguientePantalla: (partida: Partida) -> Unit,
    ) {
        onNavegarASiguientePantalla(partida)
    }

    fun borrarPartida(partida: Partida) {
        suspender {
            val respuesta = borrarPartidaCompletaUC(BorrarPartidaCompletaUC.Parametros(partida))

            if (respuesta is UC.Respuesta.Error) {
                mostrarMensaje(Mensaje(respuesta.error))
            }
        }
    }

    fun mostrarCargarEliminar(partidaClicada: Partida) {
        _mostrarCargarEliminar.value = EstadoCargarEliminar(partidaClicada)
    }

    fun ocultarCargarEliminar() {
        _mostrarCargarEliminar.value = EstadoCargarEliminar()
    }

    fun mostrarConfirmacionCarga(partida: Partida) {
        _mostrarCargarEliminar.value = EstadoCargarEliminar(partida, Finalidad.CARGAR)
    }

    fun mostrarConfirmacionBorrar(partida: Partida) {
        _mostrarCargarEliminar.value = EstadoCargarEliminar(partida, Finalidad.BORRAR)
    }

    fun ocultarConfirmacion(partida: Partida) {
        _mostrarCargarEliminar.value = EstadoCargarEliminar(partida)
    }

    fun confirmar(
        partida: Partida,
        finalidad: Finalidad,
        onNavegarASiguientePantalla: (partida: Partida) -> Unit
    ) {
        when (finalidad) {
            Finalidad.CARGAR -> continuarPartida(partida, onNavegarASiguientePantalla)
            Finalidad.BORRAR -> borrarPartida(partida)
        }

        ocultarCargarEliminar()
    }

    class EstadoCargarEliminar(
        val partidaClicada: Partida? = null,
        val finalidad: Finalidad? = null,
    ) {

        enum class Finalidad {
            CARGAR, BORRAR
        }
    }
}