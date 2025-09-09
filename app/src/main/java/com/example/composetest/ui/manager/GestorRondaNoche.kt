package com.example.composetest.ui.manager

import androidx.annotation.StringRes
import com.example.composetest.R
import com.example.composetest.extensions.joinToStringHumanReadable
import com.example.composetest.model.Jugador
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.PosibleAccionProhibida
import com.example.composetest.ui.compose.TabData
import com.example.composetest.ui.compose.navegacion.Mensaje

class GestorRondaNoche(
    override val mostrarMensaje: (mensaje: Mensaje) -> Unit
) : GestorRonda {

    override val rondaActual: Partida.Ronda = Partida.Ronda.NOCHE

    override fun getTabInicial(): TabData = TabData.EVENTOS

    @StringRes
    override fun advertenciaAccionProhibida(posibleAccionProhibida: PosibleAccionProhibida): Int? =
        when (posibleAccionProhibida) {
            is PosibleAccionProhibida.Compra, is PosibleAccionProhibida.Robo -> R.string.advertencia_asuntos_turbios
            is PosibleAccionProhibida.Reasignacion -> asignacionProhibida(posibleAccionProhibida.elemento)
            is PosibleAccionProhibida.CambioTab -> null
        }

    override fun sePuedeCambiarDeRonda(partida: Partida): Boolean {
        val validacionesComunes = validacionesComunes(partida)
        val todosTienenBaremo = todosLosJugadoresTienenBaremo(partida.jugadores.toList())
        val visitasPendientes = hayVisitasPendientes(partida)
        val validacionCompleta = (validacionesComunes + todosTienenBaremo + visitasPendientes).fold()

        if (!validacionCompleta.valido) {
            mostrarMensajeSiNoEsValido(validacionCompleta)
        }

        return validacionCompleta.valido
    }

    override fun hayQueSeleccionarEventoNuevo(hayEvento: Boolean): Boolean = !hayEvento

    private fun hayVisitasPendientes(partida: Partida): Validacion {
        val jugadoresVisitables = partida.jugadores.filter { puedeSerVisitado(it).valido }
        return Validacion(jugadoresVisitables.isEmpty(), "Adelaida debe visitar a ${jugadoresVisitables.joinToStringHumanReadable { it.nombre }}")
    }

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