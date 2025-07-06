package com.example.composetest.ui.manager

import com.example.composetest.model.Jugador

class ComprobadorNombreRepetido(private val jugadores: List<Jugador>?) {

    fun estaRepetido(nombre: String): Boolean =
        jugadores?.any { it.nombre == nombre } == true
}