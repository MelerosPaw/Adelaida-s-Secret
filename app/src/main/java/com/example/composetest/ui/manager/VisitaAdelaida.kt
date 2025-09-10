package com.example.composetest.ui.manager

import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador

const val CARTAS_NECESARIAS_PARA_SER_VISITADO = 2

fun puedeSerVisitado(jugador: Jugador) : List<Validacion> = listOf(
  ValidacionVisita.TieneUnSecretoNuevo(jugador),
  ValidacionVisita.TieneSuficientesCartas(jugador),
  ValidacionVisita.NoTieneElPerseskud(jugador)
)

sealed class ValidacionVisita(): Validacion {

  class TieneUnSecretoNuevo(private val jugador: Jugador): ValidacionVisita() {

    override fun validar(): Boolean = jugador.tienePistasPorLasQueAunNoHaSidoVisitado()
  }
  
  class TieneSuficientesCartas(private val jugador: Jugador): ValidacionVisita() {

    override fun validar(): Boolean =
      jugador.tieneSuficientesCartas(CARTAS_NECESARIAS_PARA_SER_VISITADO)
  }

  class NoTieneElPerseskud(private val jugador: Jugador): ValidacionVisita() {

    override fun validar(): Boolean = !jugador.tieneCarta(ElementoTablero.Carta.Perseskud())
  }
}