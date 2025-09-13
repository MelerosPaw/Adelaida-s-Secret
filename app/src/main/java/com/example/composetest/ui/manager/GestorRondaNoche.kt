package com.example.composetest.ui.manager

import android.content.Context
import androidx.annotation.StringRes
import com.example.composetest.R
import com.example.composetest.extensions.getPlural
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

    override fun sePuedeCambiarDeRonda(partida: Partida, context: Context): Boolean {
        val validacionesComunes = validacionesComunes(partida)
        val todosTienenBaremo = ValidacionCambioRondaNoche.TodosLosJugadoresTienenBaremo(partida.jugadores.toList())
        val visitasPendientes = ValidacionCambioRondaNoche.NoHayVisitasPendientes(partida)
        val validacionCompleta = (validacionesComunes + todosTienenBaremo + visitasPendientes)

        val sePuede = validacionCompleta.run()

        if (!sePuede) {
            mostrarMensajeSiNoEsValido(validacionCompleta, context)
        }

        return sePuede
    }

    override fun obtenerMensajesDeValidacion(validacion: Validacion, context: Context): String? =
        when (validacion) {
            is ValidacionCambioRondaNoche.TodosLosJugadoresTienenBaremo ->
                obtenerMensajeJugadoresSinBaremo(validacion, context)

            is ValidacionCambioRondaNoche.NoHayVisitasPendientes ->
                obtenerMensajeJugadoresPendientesDeSerVisitados(validacion, context)

            else -> super.obtenerMensajesDeValidacion(validacion, context)
        }

    override fun hayQueSeleccionarEventoNuevo(hayEvento: Boolean): Boolean = !hayEvento

    fun obtenerMensajeJugadoresSinBaremo(
        validacion: ValidacionCambioRondaNoche.TodosLosJugadoresTienenBaremo,
        context: Context
    ): String? = validacion.jugadoresSinBaremo
        .takeIf { it.isNotEmpty() }
        ?.let {
            context.getPlural(
                R.plurals.jugadores_sin_baremo,
                it.size,
                it.joinToStringHumanReadable { jugador -> jugador.nombre }
            )
        }

    fun obtenerMensajeJugadoresPendientesDeSerVisitados(
        pendientes: ValidacionCambioRondaNoche.NoHayVisitasPendientes,
        context: Context
    ): String = context.getString(
        R.string.adelaida_debe_visitar_a,
        pendientes.jugadoresVisitables.joinToStringHumanReadable { it.nombre })

    sealed class ValidacionCambioRondaNoche(): Validacion {
        class TodosLosJugadoresTienenBaremo(jugadores: List<Jugador>): Validacion {

            val jugadoresSinBaremo = jugadores.filter { it.idBaremo == null }

            override fun validar(): Boolean = jugadoresSinBaremo.isEmpty()
        }

        class NoHayVisitasPendientes(partida: Partida): Validacion {

            val jugadoresVisitables = partida.jugadores.filter { puedeSerVisitado(it).run() }

            override fun validar(): Boolean = jugadoresVisitables.isEmpty()
        }
    }
}