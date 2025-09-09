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

    val rondaActual: Ronda
    val mostrarMensaje: (mensaje: Mensaje) -> Unit

    fun getTabInicial(): TabData

    fun seEjecutaAhora(evento: Evento): Boolean = evento.ronda == rondaActual

    fun esOtraRonda(ronda: Ronda): Boolean = ronda != rondaActual

    // TODO Melero: 10/7/25 Pasar a cada gestor cuanddo estén
    @StringRes
    fun getPreguntaSiguienteRonda(ronda: Ronda): Int = when(ronda) {
        Ronda.MANANA -> R.string.pregunta_fin_manana
        Ronda.MEDIODIA -> R.string.pregunta_fin_mediodia
        Ronda.TARDE -> R.string.pregunta_fin_tarde
        Ronda.NOCHE -> R.string.pregunta_fin_noche
        Ronda.NO_VALIDA -> R.string.no_valido
    }

    @StringRes
    fun getSubtitulo(ronda: Ronda): Int? = when(ronda) {
        Ronda.MEDIODIA -> R.string.alias_ronda_mediodia
        Ronda.TARDE -> R.string.alias_ronda_tarde
        Ronda.NOCHE, Ronda.NO_VALIDA, Ronda.MANANA -> null
    }

    @StringRes
    fun getExplicacion(ronda: Ronda, dia: Int): Int = when(ronda) {
        Ronda.MANANA -> R.string.explicacion_manana
        Ronda.MEDIODIA -> R.string.explicacion_mediodia
        Ronda.TARDE -> R.string.explicacion_tarde
        Ronda.NOCHE -> R.string.explicacion_primera_noche.takeIf { dia == 1 } ?: R.string.explicacion_demas_noches
        Ronda.NO_VALIDA -> R.string.no_valido
    }

    fun getExplicacionEsHtml(ronda: Ronda): Boolean = when(ronda) {
        Ronda.MANANA -> false
        Ronda.MEDIODIA -> false
        Ronda.TARDE -> true
        Ronda.NOCHE -> false
        Ronda.NO_VALIDA -> false
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
        val validacionesComunes = validacionesComunes(partida).fold()
        if (!validacionesComunes.valido) {
            mostrarMensajeSiNoEsValido(validacionesComunes)
        }
        return validacionesComunes.valido
    }

    fun validacionesComunes(partida: Partida): List<Validacion> {
        val noHayJugadoresConMasPistasDelLimite = comprobarLimitePistas(partida)
        val elEventoYaSeHaRealizadoONoEsParaEstaRonda = comprobarEvento(partida)
        return listOf(noHayJugadoresConMasPistasDelLimite, elEventoYaSeHaRealizadoONoEsParaEstaRonda)
    }

    fun mostrarMensajeSiNoEsValido(validacion: Validacion) {
        validacion.mensaje?.let { mostrarMensaje(Mensaje("No se puede cambiar de ronda aún:\n\n$it")) }
    }

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

    fun comprobarEvento(partida: Partida): Validacion {
        val hayEvento = partida.eventoActual != null
        val elEventoSeHaEjecutado = partida.eventoActualEjecutado
        val elEventoVaEnEstaRonda = hayEvento && seEjecutaAhora(partida.eventoActual)

        val yaSeHaEjecutado = !hayEvento && elEventoSeHaEjecutado
        val noEsEnEstaRonda = hayEvento && !elEventoSeHaEjecutado && !elEventoVaEnEstaRonda
        val pendienteEjecucion = hayEvento && !elEventoSeHaEjecutado && elEventoVaEnEstaRonda
        val hayQueSeleccionarEventoNuevo = hayQueSeleccionarEventoNuevo(hayEvento)

        val mensaje = when {
            pendienteEjecucion -> "El evento sucede en esta ronda y aún no se ha realizado."
            hayQueSeleccionarEventoNuevo -> "Aún no se ha seleccionado evento."
            else -> null
        }
        return Validacion(yaSeHaEjecutado || noEsEnEstaRonda || (!hayEvento && !hayQueSeleccionarEventoNuevo), mensaje)
    }

    fun hayQueSeleccionarEventoNuevo(hayEvento: Boolean): Boolean = false

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
                Ronda.NO_VALIDA -> null
            }
        }
    }


}