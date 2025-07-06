package com.example.composetest.ui.manager

import androidx.annotation.StringRes
import com.example.composetest.R
import com.example.composetest.model.Evento
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.PosibleAccionProhibida
import com.example.composetest.ui.compose.TabData
import com.example.composetest.ui.compose.navegacion.Mensaje

class GestorRondaMediodia(
    override val mostrarMensaje: (Mensaje) -> Unit
) : GestorRonda {

    override fun getTabInicial(): TabData = TabData.TABLERO

    override fun seEjecutaAhora(evento: Evento): Boolean = evento.ronda == Partida.Ronda.MEDIODIA

    @StringRes
    override fun advertenciaAccionProhibida(posibleAccionProhibida: PosibleAccionProhibida): Int? =
        when (posibleAccionProhibida) {
            is PosibleAccionProhibida.Compra,
            is PosibleAccionProhibida.Robo -> R.string.advertencia_asuntos_turbios
            is PosibleAccionProhibida.CambioTab -> null
            is PosibleAccionProhibida.Reasignacion -> posibleAccionProhibida.elemento
                .takeIf { posibleAccionProhibida.tabActual == TabData.JUGADORES }
                ?.let(::asignacionProhibida)
        }
}