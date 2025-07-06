package com.example.composetest.ui.manager

import androidx.annotation.StringRes
import com.example.composetest.R
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Evento
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.PosibleAccionProhibida
import com.example.composetest.ui.compose.TabData
import com.example.composetest.ui.compose.navegacion.Mensaje

class GestorRondaTarde(
    override val mostrarMensaje: (mensaje: Mensaje) -> Unit
): GestorRonda {

    override fun getTabInicial(): TabData = TabData.JUGADORES

    override fun seEjecutaAhora(evento: Evento): Boolean = evento.ronda == Partida.Ronda.TARDE

    @StringRes
    override fun advertenciaAccionProhibida(posibleAccionProhibida: PosibleAccionProhibida): Int? =
        when (posibleAccionProhibida) {
            is PosibleAccionProhibida.CambioTab -> R.string.advertencia_cambio_tab_tablero
                .takeIf { posibleAccionProhibida.pagina == TabData.TABLERO }
            is PosibleAccionProhibida.Compra -> null
            is PosibleAccionProhibida.Robo -> null
            is PosibleAccionProhibida.Reasignacion -> posibleAccionProhibida.elemento
                .takeIf { posibleAccionProhibida.tabActual == TabData.TABLERO }
                ?.let(::asignacionProhibida)
                ?: super.asignacionProhibida(posibleAccionProhibida.elemento)
        }

    @StringRes
    override fun asignacionProhibida(elemento: ElementoTablero): Int = when (elemento) {
        is ElementoTablero.Carta -> R.string.advertencia_reasignacion_carta_tarde
        is ElementoTablero.Pista -> R.string.advertencia_reasignacion_pista_tarde
    }
}