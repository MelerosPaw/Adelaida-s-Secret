package com.example.composetest.ui.manager

import com.example.composetest.model.CARTAS_NECESARIAS_PARA_SER_VISITADO
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.navegacion.JugadorModelo

fun puedeSerVisitado(jugador: Jugador): Validador<ValidacionVisita> = Validador(
  listOf(
    ValidacionVisita.TieneUnSecretoNuevo(jugador),
    ValidacionVisita.TieneSuficientesCartas(jugador),
    ValidacionVisita.NoTieneElPerseskud(jugador)
  )
)

/** ¿Debe ser visitado? */
sealed class ValidacionVisita(): Validacion {

  /** Tiene un secreto nuevo. */
  class TieneUnSecretoNuevo(jugador: Jugador): ValidacionVisita() {

    val result by lazy { jugador.tienePistasPorLasQueAunNoHaSidoVisitado() }

    override fun validar(): Boolean = result
  }

  /** Tiene más de [com.example.composetest.model.CARTAS_NECESARIAS_PARA_SER_VISITADO]. */
  class TieneSuficientesCartas(jugador: Jugador): ValidacionVisita() {

    val result by lazy { jugador.tieneSuficientesCartas(CARTAS_NECESARIAS_PARA_SER_VISITADO) }

    override fun validar(): Boolean = result
  }

  /** No tener la carta Perseskud. */
  class NoTieneElPerseskud(jugador: Jugador): ValidacionVisita() {

    val result by lazy { !jugador.tieneCarta(ElementoTablero.Carta.Perseskud()) }

    override fun validar(): Boolean = result
  }
}

sealed class InfoVisita() {
  object Cargando: InfoVisita()
  object NadieParaVisitar: InfoVisita()
  class Info(val list: List<Jugador>): InfoVisita()

  class Jugador(val jugador: JugadorModelo, val validador: Validador<ValidacionVisita>)
}