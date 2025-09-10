package com.example.composetest.ui.manager

import android.content.Context
import androidx.annotation.StringRes
import com.example.composetest.R
import com.example.composetest.extensions.getPlural
import com.example.composetest.extensions.joinToStringHumanReadable
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Evento
import com.example.composetest.model.Jugador
import com.example.composetest.model.PISTAS_MAXIMAS_EN_LA_VITRINA
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

    fun hayQueSeleccionarEventoNuevo(hayEvento: Boolean): Boolean = false

    /**
     * Se puede seguir solo si:
     * * Todos los jugadores tienen 3 pistas como mucho.
     * * No hay evento o no es para esta ronda, o ya se ha realizado.
     */
    fun sePuedeCambiarDeRonda(partida: Partida, context: Context): Boolean {
        val validacionesComunes = validacionesComunes(partida)

        if (!validacionesComunes.run()) {
            mostrarMensajeSiNoEsValido(validacionesComunes, context)
        }
        return validacionesComunes.run()
    }

    fun validacionesComunes(partida: Partida): List<Validacion> {
        val limitePistas = ValidacionCambioRonda.NadieRebasaElLimiteDePistas(partida)
        val okWithEvent = ValidacionCambioRonda.EventoYaRalizadoONoTocaAhora(partida,
            ::seEjecutaAhora, ::hayQueSeleccionarEventoNuevo)
        return listOf(limitePistas, okWithEvent)
    }

    fun mostrarMensajeSiNoEsValido(validaciones: List<Validacion>, context: Context) {
        val mensajeFormateado: String? = validaciones
            .filter { !it.validar() }
            .mapNotNull { obtenerMensajesDeValidacion(it, context) }
            .joinToString("\n") { "\t- $it" }
            .takeIf { it.isNotBlank() }

        mensajeFormateado?.let { mostrarMensaje(Mensaje("No se puede cambiar de ronda aún:\n\n$it")) }
    }

    fun obtenerMensajesDeValidacion(validacion: Validacion, context: Context): String? =
        when (validacion) {
            is ValidacionCambioRonda.NadieRebasaElLimiteDePistas ->
                obtenerMensajeExcesoPistas(validacion.quienesRebasan, context)

            is ValidacionCambioRonda.EventoYaRalizadoONoTocaAhora ->
                obtenerMensajeEventoRealizado(validacion, context)

            else -> null
        }

    private fun obtenerMensajeEventoRealizado(
        validacion: ValidacionCambioRonda.EventoYaRalizadoONoTocaAhora,
        context: Context
    ): String? = when {
        validacion.pendienteEjecucion -> context.getString(R.string.evento_pendiente_de_ejecucion)
        validacion.seNecesitaEventoNuevo -> context.getString(R.string.evento_no_seleccionado_aun)
        else -> null
    }

    private fun obtenerMensajeExcesoPistas(
        jugadoresConDemasiadas: List<Jugador>,
        context: Context
    ): String? =
        jugadoresConDemasiadas
            .joinToStringHumanReadable { it.nombre }
            .takeIf { it.isNotEmpty() }
            ?.let {
                context.getPlural(
                    R.plurals.jugadores_con_mas_pistas_de_las_debidas,
                    jugadoresConDemasiadas.size,
                    it,
                    PISTAS_MAXIMAS_EN_LA_VITRINA
                )
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
                Ronda.NO_VALIDA -> null
            }
        }
    }

    sealed class ValidacionCambioRonda(): Validacion {

        class NadieRebasaElLimiteDePistas(val partida: Partida) : ValidacionCambioRonda() {

            val quienesRebasan: List<Jugador>
                get() = partida.jugadores.filter { it.tieneDemasiadasPistas() }

            override fun validar(): Boolean = quienesRebasan.isEmpty()
        }

        class EventoYaRalizadoONoTocaAhora(
            val partida: Partida,
            val seEjecutaAhora: (Evento) -> Boolean,
            val hayQueSeleccionarEventoNuevo: (hayEvento: Boolean) -> Boolean
        ): ValidacionCambioRonda() {

            var pendienteEjecucion: Boolean = false
            var seNecesitaEventoNuevo: Boolean = false

            override fun validar(): Boolean {
                val hayEvento = partida.eventoActual != null
                val elEventoSeHaEjecutado = partida.eventoActualEjecutado
                val elEventoVaEnEstaRonda = hayEvento && seEjecutaAhora(partida.eventoActual)

                val yaSeHaEjecutado = !hayEvento && elEventoSeHaEjecutado
                val noEsEnEstaRonda = hayEvento && !elEventoSeHaEjecutado && !elEventoVaEnEstaRonda
                pendienteEjecucion = hayEvento && !elEventoSeHaEjecutado && elEventoVaEnEstaRonda
                seNecesitaEventoNuevo = hayQueSeleccionarEventoNuevo(hayEvento)

                return yaSeHaEjecutado || noEsEnEstaRonda || (!hayEvento && !seNecesitaEventoNuevo)
            }
        }
    }

}