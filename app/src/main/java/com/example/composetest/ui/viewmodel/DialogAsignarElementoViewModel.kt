package com.example.composetest.ui.viewmodel

import com.example.composetest.data.uc.AsignarElementoUC
import com.example.composetest.data.uc.UC
import com.example.composetest.extensions.plusIf
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.dialog.Resultado
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

private const val CANCELAR = "Cancelar"
private const val TABLERO = "Devolver al tablero"

@HiltViewModel
class DialogAsignarElementoViewModel @Inject constructor() : BaseViewModel() {

    @Inject
    lateinit var asignarElementoUC: Provider<AsignarElementoUC>

    fun getOpcionesParaAsignar(
        jugadores: List<Jugador>,
        poseedor: Jugador?,
        elemento: ElementoTablero,
        existe: Boolean,
        idPartida: Long,
        onDismiss: (Resultado) -> Unit,
    ): Array<OpcionDialogo<Jugador?>> {
        val opcionJugador: MutableList<OpcionDialogo<Jugador?>> = jugadores.map { jugador ->
            crearOpcion(jugador, jugador.nombre, elemento, idPartida, existe, onDismiss)
        }.toMutableList()

        poseedor?.nombre?.let { nombrePoseedor -> opcionJugador.removeIf { it.texto == nombrePoseedor } }

        val devolverAlTablero = crearOpcion(null, TABLERO, elemento, idPartida, existe, onDismiss)
        val cancelar = crearOpcion(null, CANCELAR, elemento, idPartida, existe, onDismiss)

        return opcionJugador
            .plusIf(devolverAlTablero, poseedor != null && elemento.sePuedeDevolverAlTablero())
            .plus(cancelar)
            .toTypedArray()
    }

    fun asignarAJugador(
        nombreJugador: String,
        elemento: ElementoTablero,
        partida: Long,
        existia: Boolean,
        onDismiss: (Resultado) -> Unit,
    ) {
        if (nombreJugador == CANCELAR) {
            onDismiss(Resultado.Dismiss)

        } else {
            suspender {
                val nombre = nombreJugador.takeIf { it != TABLERO }
                val parametros = AsignarElementoUC.Parametros(elemento, nombre, partida, true, existia)
                val respuesta = asignarElementoUC.get().invoke(parametros)

                when (respuesta) {
                    is UC.Respuesta.Valor -> onDismiss(Resultado.Asignado(elemento, nombre))
                    is UC.Respuesta.Error -> mostrarErrorAsignacion(respuesta.error, onDismiss)
                }
            }
        }
    }

    private fun mostrarErrorAsignacion(error: String, onDismiss: (Resultado) -> Unit) {
        mostrarMensaje(Mensaje(error))
        onDismiss(Resultado.Error())
    }

    private fun crearOpcion(
        jugador: Jugador?,
        nombre: String,
        elemento: ElementoTablero,
        idPartida: Long,
        existe: Boolean,
        onDismiss: (Resultado) -> Unit
    ): OpcionDialogo<Jugador?> = OpcionDialogo<Jugador?>(
        nombre,
        jugador,
        jugador == null || !jugador.tieneDemasiadasPistas()
    ) {
        asignarAJugador(nombre, elemento, idPartida, existe, onDismiss)
    }
}
