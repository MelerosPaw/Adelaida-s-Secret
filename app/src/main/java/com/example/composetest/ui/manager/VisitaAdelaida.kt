package com.example.composetest.ui.manager

import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador

const val CARTAS_NECESARIAS_PARA_SER_VISITADO = 2

fun puedeSerVisitado(jugador: Jugador) : Validacion {
  val tieneUnSecrtoNuevo = Validacion(
    jugador.tienePistasPorLasQueAunNoHaSidoVisitado(),
    "El jugador no tiene ningún secreto nuevo"
  )

  val tieneSuficientesCartas = Validacion(
    jugador.tieneSuficientesCartas(CARTAS_NECESARIAS_PARA_SER_VISITADO),
    "El jugador no tiene suficientes cartas para ser visitado"
  )

  val noTieneElPerseskud = Validacion(
    !jugador.tieneCarta(ElementoTablero.Carta.Perseskud()),
    "El jugador no puede ser visitado porque tiene el Perseskud"
  )

  return listOf(tieneUnSecrtoNuevo, tieneSuficientesCartas, noTieneElPerseskud).fold()
}