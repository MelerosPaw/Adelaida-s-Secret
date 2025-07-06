package com.example.composetest.ui.manager

import androidx.compose.runtime.Composable
import com.example.composetest.model.ElementoTablero.Pista
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.dialog.ContenedorDialogDevolverATablero
import com.example.composetest.ui.compose.dialog.ContenedorDialogReemplazo

class GestorPistaPendiente(
    val pistaPendiente: Pista,
    val poseedor: Jugador,
    val idPartida: Long,
    val mostrarConfirmacion: (Pista, Accion) -> Unit
) {

    fun onMostrarConfirmacion(poseedor: Jugador, accion: Accion) {
        if (poseedor.esElMismoQue(this.poseedor)) {
            mostrarConfirmacion.invoke(pistaPendiente, accion)
        }
    }

    fun ejecutarAccion(accion: Accion) {
        accion.ejecutarAccion.invoke(pistaPendiente, poseedor, idPartida)
    }

    sealed class Accion(
        val ejecutarAccion: (
            pistaPendiente: Pista,
            poseedor: Jugador,
            idPartida: Long
        ) -> Unit,
        private val mensajeConfirmacion: (pistaPendiente: Pista) -> String,
        val cabecera: @Composable (pistaPendiente: Pista) -> Unit,
    ) {

        fun getMensajeConfirmacion(pistaPendiente: Pista) = mensajeConfirmacion.invoke(pistaPendiente)

        class DevolucionATablero(onDevolver: (Pista) -> Unit): Accion(
            { pista, _, _ -> onDevolver(pista) },
            { "¿Quieres devolver la pista ${it.id} al tablero?" },
            { pista -> ContenedorDialogDevolverATablero(pista) }
        )

        class Reemplazo(
            val pistaADevolverAlTablero: Pista,
            onReemplazar: (Pista, Pista, Jugador, Long) -> Unit,
        ): Accion(
            { pista, poseedor, idPartida -> onReemplazar(pista, pistaADevolverAlTablero, poseedor, idPartida) },
            { "¿Quieres quedarte con la pista ${it.id} y devolver la ${pistaADevolverAlTablero.id} al tablero?" },
            { pista -> ContenedorDialogReemplazo(pista, pistaADevolverAlTablero) }
        )
    }
}