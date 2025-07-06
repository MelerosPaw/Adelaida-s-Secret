package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.composetest.data.uc.AsignarAsesinoUC
import com.example.composetest.model.Partida
import com.example.composetest.model.Sospechoso
import com.example.composetest.ui.compose.navegacion.SeleccionAsesino
import com.example.composetest.ui.compose.screen.VisualizacionSospechoso
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SeleccionAsesinoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): BaseViewModel() {

    @Inject
    lateinit var asignarAsesino: AsignarAsesinoUC

    private val _mostrarConfirmacionSospechoso: MutableState<Sospechoso?> = mutableStateOf(null)
    val mostrarConfirmacionSospechoso: State<Sospechoso?> = _mostrarConfirmacionSospechoso
    private val _visualizacion: MutableState<VisualizacionSospechoso> =
        mutableStateOf(VisualizacionSospechoso.Lista(1))
    val visualizacion: State<VisualizacionSospechoso> = _visualizacion
    
    private val idPartida = savedStateHandle.toRoute<SeleccionAsesino>().idPartida

    fun onCambiarColumnas(columnas: Int) {
        val actual = _visualizacion.value

        if (columnas != actual.columnas) {
            _visualizacion.value = when (actual) {
                is VisualizacionSospechoso.Lista -> VisualizacionSospechoso.Lista(columnas)
                is VisualizacionSospechoso.LibroSecretos ->
                    VisualizacionSospechoso.LibroSecretos(columnas)
            }
        }
    }

    fun onCambiarVisualizacion(visualizacion: VisualizacionSospechoso) {
        val actual = _visualizacion.value

        if (actual::class != visualizacion::class) {
            _visualizacion.value = visualizacion
        }
    }

    fun seleccionarSospechoso(sospechoso: Sospechoso) {
        _mostrarConfirmacionSospechoso.value = sospechoso
    }

    fun confirmarSospechoso(
        sospechoso: Sospechoso,
        onNavegarAlSiguientePaso: (partida: Partida) -> Unit
    ) {
        suspender {
            asignarAsesino(AsignarAsesinoUC.Parametros(idPartida, sospechoso))
                .usarRespuesta{  asesinoAsignado(it, onNavegarAlSiguientePaso) }
        }
    }

    private fun asesinoAsignado(partida: Partida, onNavegarAlSiguientePaso: (partida: Partida) -> Unit) {
        onNavegarAlSiguientePaso(partida)
        ocultarDialogoConfirmacion()
    }

    fun ocultarDialogoConfirmacion() {
        _mostrarConfirmacionSospechoso.value = null
    }

}