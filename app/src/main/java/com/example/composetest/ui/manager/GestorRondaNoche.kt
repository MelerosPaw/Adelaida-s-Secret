package com.example.composetest.ui.manager

import androidx.annotation.StringRes
import com.example.composetest.extensions.joinToStringHumanReadable
import com.example.composetest.model.Evento
import com.example.composetest.model.Jugador
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.PosibleAccionProhibida
import com.example.composetest.ui.compose.TabData
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.manager.GestorRonda.Validacion

class GestorRondaNoche(
    override val mostrarMensaje: (mensaje: Mensaje) -> Unit
) : GestorRonda {

    override fun getTabInicial(): TabData = TabData.EVENTOS

    override fun seEjecutaAhora(evento: Evento): Boolean = evento.ronda == Partida.Ronda.NOCHE

    @StringRes
    override fun advertenciaAccionProhibida(posibleAccionProhibida: PosibleAccionProhibida): Int? = null

    override fun sePuedeCambiarDeRonda(partida: Partida): Boolean {
        val sePuede = super.sePuedeCambiarDeRonda(partida)

        return if (sePuede) {
            val todosTienenBaremo = todosLosJugadoresTienenBaremo(partida.jugadores.toList())
            val hayVisitasPendientes = this@GestorRondaNoche.hayVisitasPendientes()
            mostrarMensajeSiNoEsValido(todosTienenBaremo, hayVisitasPendientes)
            todosTienenBaremo.valido && !hayVisitasPendientes.valido
        } else {
            false
        }
    }

    // TODO Melero: 5/3/25
    private fun hayVisitasPendientes(): Validacion = Validacion(false, "Quedan las visitas")

    private fun todosLosJugadoresTienenBaremo(jugadores: List<Jugador>): Validacion {
        val jugadoresSinBaremos = jugadores
            .filter { it.idBaremo == null }

        val mensaje = if (jugadoresSinBaremos.isNotEmpty()) {
            val cantidad = jugadoresSinBaremos.size
            val nombres = jugadoresSinBaremos.joinToStringHumanReadable { it.nombre }
            val tienen = "tienen".takeIf { cantidad > 1 } ?: "tiene"
            "$nombres aún no $tienen un baremo seleccionado."
        } else {
            null
        }

        return Validacion(jugadoresSinBaremos.isEmpty(), mensaje)
    }
}