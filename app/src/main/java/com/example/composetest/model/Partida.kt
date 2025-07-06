package com.example.composetest.model

import com.example.composetest.extensions.formatearFecha
import com.example.composetest.extensions.formatearSoloFecha
import com.example.composetest.extensions.joinToStringHumanReadable
import java.util.Date

/**
 * @property jugadores Para crear una partida hace falta una serie de jugadores.
 * @property nombre El nombre de la partida será lo que la identifique.
 */
data class Partida(
    val id: Long,
    val estadoCreacion: EstadoCreacion,
    val ronda: Ronda,
    val dia: Int = 1,
    val jugadores: Array<Jugador> = emptyArray(),
    val tablero: Tablero? = null,
    val asesino: Sospechoso? = null,
    val fecha: Date = Date(),
    val nombre: String = formatearFecha(fecha),
    val fuerzaDefensa: Int = 6,
    val eventoActual: Evento? = null,
    val eventoActualEjecutado: Boolean = false,
    val eventosConsumidos: Set<Evento> = emptySet(),
) {

    fun info(): String {
        val fecha = formatearSoloFecha(fecha)
        val nombreRonda = (ronda.id.takeIf {
            estadoCreacion == EstadoCreacion.PARTIDA_EMPEZADA
        } ?: estadoCreacion.id).uppercase()
        val jugadores = jugadores
            .takeIf { it.isNotEmpty() }
            ?.let { "\nJugadores: ${it.joinToStringHumanReadable { it.nombre }}" }
            .orEmpty()

        return "$fecha - $nombreRonda$jugadores"
    }

    enum class EstadoCreacion(val id: String) {
        SELECCION_ASESINO("Seleccion de sospechoso"),
        SELECCION_TABLERO("Creación de tablero"),
        SELECCION_JUGADORES("Listado de jugadores"),
        PARTIDA_EMPEZADA("Jugando"),
        NO_VALIDO("No valido");

        companion object {

            fun byId(id: String): EstadoCreacion =
                EstadoCreacion.entries.firstOrNull { it.id == id } ?: NO_VALIDO
        }
    }

    enum class Ronda(val id: String) {
        MANANA("Mañana"),
        MEDIODIA("Mediodía"),
        TARDE("Tarde"),
        NOCHE("Noche"),
        NO_VALIDO("No valido");

        companion object {

            fun byId(id: String): Ronda =
                Ronda.entries.firstOrNull { it.id == id } ?: NO_VALIDO
        }
    }
}