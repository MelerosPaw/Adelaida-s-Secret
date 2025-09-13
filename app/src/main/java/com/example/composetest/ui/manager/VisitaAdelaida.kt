package com.example.composetest.ui.manager

import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.navegacion.JugadorModelo

const val CARTAS_NECESARIAS_PARA_SER_VISITADO = 2

fun puedeSerVisitado(jugador: Jugador) : List<ValidacionVisita> = listOf(
  ValidacionVisita.TieneUnSecretoNuevo(jugador),
  ValidacionVisita.TieneSuficientesCartas(jugador),
  ValidacionVisita.NoTieneElPerseskud(jugador)
)

sealed class ValidacionVisita(): Validacion {

  class TieneUnSecretoNuevo(jugador: Jugador): ValidacionVisita() {

    val result by lazy { jugador.tienePistasPorLasQueAunNoHaSidoVisitado() }

    override fun validar(): Boolean = result
  }
  
  class TieneSuficientesCartas(jugador: Jugador): ValidacionVisita() {

    val result by lazy { jugador.tieneSuficientesCartas(CARTAS_NECESARIAS_PARA_SER_VISITADO) }

    override fun validar(): Boolean = result
  }

  class NoTieneElPerseskud(jugador: Jugador): ValidacionVisita() {

    val result by lazy { !jugador.tieneCarta(ElementoTablero.Carta.Perseskud()) }

    override fun validar(): Boolean = result
  }
}

sealed class InfoVisita() {
  object Cargando: InfoVisita()
  object NadieParaVisitar: InfoVisita()
  class Info(val list: List<Jugador>): InfoVisita()

  class Jugador(val jugador: JugadorModelo, val validaciones: List<ValidacionVisita>)
}