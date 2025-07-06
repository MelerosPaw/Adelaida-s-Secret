package com.example.composetest.data.mapper.todbo

import com.example.composetest.data.db.reduced.AsignacionElemento
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.ElementoTablero.Carta
import com.example.composetest.model.ElementoTablero.Pista

fun ElementoTablero.cambiarPoseedor(
    idJugador: Long?,
    idPartida: Long,
    idSecretoParaGuardar: String?,
): AsignacionElemento = when (this) {
    is Carta -> cambiarPoseedor(idJugador, idPartida, idSecretoParaGuardar)
    is Pista -> cambiarPoseedor(idJugador, idPartida, idSecretoParaGuardar)
}