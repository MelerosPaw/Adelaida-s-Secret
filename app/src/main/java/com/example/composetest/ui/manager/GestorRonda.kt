package com.example.composetest.ui.manager

import androidx.annotation.StringRes
import com.example.composetest.R
import com.example.composetest.extensions.joinToStringHumanReadable
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Evento
import com.example.composetest.model.Jugador
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.PosibleAccionProhibida
import com.example.composetest.ui.compose.TabData
import com.example.composetest.ui.compose.navegacion.Mensaje

interface GestorRonda {

    val mostrarMensaje: (mensaje: Mensaje) -> Unit

    fun getTabInicial(): TabData

    fun seEjecutaAhora(evento: Evento): Boolean

    @StringRes
    fun advertenciaAccionProhibida(posibleAccionProhibida: PosibleAccionProhibida): Int?

    @StringRes
    fun asignacionProhibida(elemento: ElementoTablero): Int = when (elemento) {
        is ElementoTablero.Carta -> R.string.advertencia_reasignacion_carta_mediodia
        is ElementoTablero.Pista -> R.string.advertencia_reasignacion_pista_mediodia
    }

    /**
     * Se puede seguir solo si:
     * * Todos los jugadores tienen 3 pistas como mucho.
     * * No hay evento o no es para esta ronda, o ya se ha realizado.
     */
    fun sePuedeCambiarDeRonda(partida: Partida): Boolean {
        val noHayJugadoresConMasPistasDelLimite = comprobarLimitePistas(partida)
        val elEventoYaSeHaRealizadoONoEsParaEstaRonda = comprobarEvento(partida)
        mostrarMensajeSiNoEsValido(noHayJugadoresConMasPistasDelLimite, elEventoYaSeHaRealizadoONoEsParaEstaRonda)
        return noHayJugadoresConMasPistasDelLimite.valido && elEventoYaSeHaRealizadoONoEsParaEstaRonda.valido
    }

    fun mostrarMensajeSiNoEsValido(vararg validaciones: Validacion) {
        validaciones
            .mapNotNull { it.mensaje }
            .joinToString("\n") { "\t- $it" }
            .takeIf { it.isNotBlank() }
            ?.let { mostrarMensaje(Mensaje("No se puede cambiar de ronda aún:\n\n$it")) }
    }

    fun esOtraRonda(ronda: Partida.Ronda): Boolean

    private fun comprobarLimitePistas(partida: Partida): Validacion {
        val jugadoresConDemasiadasPistas = partida.jugadores.filter { it.tieneDemasiadasPistas() }
        val vale = jugadoresConDemasiadasPistas.isEmpty()
        val mensaje = jugadoresConDemasiadasPistas.takeIf { !vale }?.let { mostrarJugadoresConDemasiadasPistas(it) }
        return Validacion(vale, mensaje)
    }

    private fun mostrarJugadoresConDemasiadasPistas(jugadores: List<Jugador>): String {
        val nombresDeJugadores = jugadores.joinToStringHumanReadable { it.nombre }
        val tienen = "tienen".takeIf { jugadores.size > 1 } ?: "tiene"
        val deben = "Deben".takeIf { jugadores.size > 1 } ?: "Debe"
        return "$nombresDeJugadores aún $tienen más de tres pistas en la vitrina. " +
            "$deben deshacerse de una de ellas para poder continuar."
    }

    private fun comprobarEvento(partida: Partida): Validacion {
        val noHayEvento = partida.eventoActual == null
        val elEventoSeHaEjecutado = partida.eventoActualEjecutado
        val elEventoNoVaEnEstaRonda = !noHayEvento && !seEjecutaAhora(partida.eventoActual)
        val okWithEvent = noHayEvento || elEventoSeHaEjecutado || elEventoNoVaEnEstaRonda

        val mensaje = "El evento sucede en esta ronda y aún no se ha realizado.".takeIf { !okWithEvent }
        return Validacion(okWithEvent, mensaje)
    }

    class Factory {

        companion object {

            fun from(
                ronda: Partida.Ronda,
                mostrarMensaje: (mensaje: Mensaje) -> Unit,
            ): GestorRonda? = when (ronda) {
                Partida.Ronda.MEDIODIA -> GestorRondaMediodia(mostrarMensaje)
                Partida.Ronda.TARDE -> GestorRondaTarde(mostrarMensaje)
                Partida.Ronda.NOCHE -> GestorRondaNoche(mostrarMensaje)
                else -> null /* No hay más rondas que usen la misma pantalla. */
            }
        }
    }

    class Validacion(val valido: Boolean, val mensaje: String?)
}