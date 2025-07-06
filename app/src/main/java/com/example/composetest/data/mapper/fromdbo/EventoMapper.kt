package com.example.composetest.data.mapper.fromdbo

import com.example.composetest.data.db.dbo.EventoDBO
import com.example.composetest.model.Evento
import com.example.composetest.model.Evento.MaxGanadores
import com.example.composetest.model.Partida
import com.example.composetest.model.PuntuacionFormateada
import com.example.composetest.model.Resultados

fun EventoDBO.toModel(): Evento? = obtenerAccion(this)?.let { accion ->
  val puntuacion = explicacion.formatearExplicacion()
  Evento(
    ronda = Partida.Ronda.byId(ronda),
    nombre = nombre,
    explicacion = puntuacion.explicacionFormateada,
    puntuaciones = puntuacion.puntuaciones,
    maxGanadores = when (maxGanadores) {
      MaxGanadores.Todos.cantidad -> MaxGanadores.Todos
      MaxGanadores.Nadie.cantidad -> MaxGanadores.Nadie
      else -> MaxGanadores.CantidadDeterminada(maxGanadores)
    },
    accion = accion
  )
}

private fun obtenerAccion(evento: EventoDBO): Evento.Accion? {
  val accion = when {
    evento.idAccion.startsWith("EF") -> crearAccionAplicarEfecto(evento)
    evento.idAccion.startsWith("CO") -> crearAccionObtenerComodin(evento)
    else -> null
  }
  return accion
}

private fun crearAccionAplicarEfecto(evento: EventoDBO): Evento.Accion.AplicarEfecto? {
  return obtenerEfecto(evento)?.let(Evento.Accion::AplicarEfecto)
}

private fun crearAccionObtenerComodin(evento: EventoDBO): Evento.Accion.OtorgarComodin? =
  evento.idAccion.toComodin()
    ?.let(Evento.Accion::OtorgarComodin)

private fun obtenerEfecto(evento: EventoDBO): Evento.Efecto? = evento.idAccion.toEfecto()

private fun String.formatearExplicacion(): PuntuacionFormateada {
  val tipoResultado = Resultados.entries.firstOrNull {
    contains(it.placehoder)
  }
  return tipoResultado?.formatear(this) ?: PuntuacionFormateada(this, "")
}