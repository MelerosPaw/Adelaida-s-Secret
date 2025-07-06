package com.example.composetest.ui.compose.navegacion

import kotlinx.serialization.Serializable

@Serializable
object MenuPrincipal

/** Solo identificativo */
interface PasoCreacion

@Serializable
object SeleccionNombre : PasoCreacion

@Serializable
data class SeleccionAsesino(val idPartida: Long) : PasoCreacion

@Serializable
data class SeleccionJugadores(val idPartida: Long, val nombrePartida: String) : PasoCreacion

@Serializable
data class NuevoTablero(val idPartida: Long?) : PasoCreacion

@Serializable
data class Partida(val idPartida: Long, val nombre: String) : PasoCreacion

@Serializable
object CargarPartida

@Serializable
data class Mensaje(val mensaje: String)