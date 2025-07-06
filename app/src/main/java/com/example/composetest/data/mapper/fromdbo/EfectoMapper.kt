package com.example.composetest.data.mapper.fromdbo

import com.example.composetest.model.Evento

fun String.toEfecto(): Evento.Efecto? = when (this) {
  "EF1" -> Evento.Efecto.DobleInvestigacion
  "EF2" -> Evento.Efecto.DobleRobo
  "EF3" -> Evento.Efecto.DobleSicario
  "EF4" -> Evento.Efecto.InhabilitadoTodoElDia
  "EF5" -> Evento.Efecto.ProteccionContraAdelaida
  "EF6" -> Evento.Efecto.CancelarTarde
  "EF7" -> Evento.Efecto.RevelarHabitacionElUltimo
  "EF8" -> Evento.Efecto.VerPistas
  "EF9" -> Evento.Efecto.ProteccionContraAsuntosTurbios
  else -> null
}