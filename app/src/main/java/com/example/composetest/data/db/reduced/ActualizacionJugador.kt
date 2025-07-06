package com.example.composetest.data.db.reduced

data class ActualizacionJugador(
    val nombre: String,
    val partida: Long,
    val dinero: Int,
    val id: Long = 0L
)