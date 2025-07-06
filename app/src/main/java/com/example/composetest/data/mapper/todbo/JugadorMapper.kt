package com.example.composetest.data.mapper.todbo

import com.example.composetest.data.db.reduced.ActualizacionJugador
import com.example.composetest.model.Jugador

fun Jugador.toActualizacion(idPartida: Long, dinero: Int = this.dinero): ActualizacionJugador =
    ActualizacionJugador(nombre, idPartida, dinero)