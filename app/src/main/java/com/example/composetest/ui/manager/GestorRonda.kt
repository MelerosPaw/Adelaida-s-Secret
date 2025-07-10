package com.example.composetest.ui.manager

import androidx.annotation.StringRes
import com.example.composetest.R
import com.example.composetest.extensions.joinToStringHumanReadable
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Evento
import com.example.composetest.model.Jugador
import com.example.composetest.model.Partida
import com.example.composetest.model.Partida.Ronda
import com.example.composetest.ui.compose.PosibleAccionProhibida
import com.example.composetest.ui.compose.TabData
import com.example.composetest.ui.compose.navegacion.Mensaje

interface GestorRonda {

    val mostrarMensaje: (mensaje: Mensaje) -> Unit

    fun getTabInicial(): TabData

    fun seEjecutaAhora(evento: Evento): Boolean

    // TODO Melero: 10/7/25 Pasar a cada gestor cuanddo estén
    @StringRes
    fun getPreguntaSiguienteRonda(ronda: Ronda): Int = when(ronda) {
        Ronda.MANANA -> R.string.pregunta_fin_manana
        Ronda.MEDIODIA -> R.string.pregunta_fin_mediodia
        Ronda.TARDE -> R.string.pregunta_fin_tarde
        Ronda.NOCHE -> R.string.pregunta_fin_noche
        Ronda.NO_VALIDO -> R.string.no_valido
    }

    @StringRes
    fun getSubtitulo(ronda: Ronda): Int? = when(ronda) {
        Ronda.MEDIODIA -> R.string.alias_ronda_mediodia
        Ronda.TARDE -> R.string.alias_ronda_tarde
        Ronda.NOCHE, Ronda.NO_VALIDO, Ronda.MANANA -> null
    }

    @StringRes
    fun getExplicacion(ronda: Ronda): Int = when(ronda) {
        Ronda.MANANA -> R.string.explicacion_manana
        Ronda.MEDIODIA -> R.string.explicacion_mediodia
        Ronda.TARDE -> R.string.explicacion_tarde
        Ronda.NOCHE -> R.string.explicacion_primera_noche
        Ronda.NO_VALIDO -> R.string.no_valido
    }

    fun getExplicacionEsHtml(ronda: Ronda): Boolean = when(ronda) {
        Ronda.MANANA -> false
        Ronda.MEDIODIA -> false
        Ronda.TARDE -> true
        Ronda.NOCHE -> false
        Ronda.NO_VALIDO -> false
    }

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
                ronda: Ronda,
                mostrarMensaje: (mensaje: Mensaje) -> Unit,
            ): GestorRonda? = when (ronda) {
                Ronda.MEDIODIA -> GestorRondaMediodia(mostrarMensaje)
                Ronda.TARDE -> GestorRondaTarde(mostrarMensaje)
                Ronda.NOCHE -> GestorRondaNoche(mostrarMensaje)
                Ronda.MANANA -> null // TODO Melero: 10/7/25 Por hacer
                Ronda.NO_VALIDO -> null
            }
        }
    }

    class Validacion(val valido: Boolean, val mensaje: String?)
}