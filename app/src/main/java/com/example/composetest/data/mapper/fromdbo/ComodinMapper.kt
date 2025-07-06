package com.example.composetest.data.mapper.fromdbo

import com.example.composetest.model.Evento

fun String.toComodin(): Evento.Comodin? = when (this) {
  "CO1" -> Evento.Comodin.GuantesBlancos
  "CO2" -> Evento.Comodin.DescuentoEnSicarios
  "CO3" -> Evento.Comodin.AcusacionFalsa
  "CO4" -> Evento.Comodin.InciensoProtector
  "CO5" -> Evento.Comodin.Somnifero
  else -> null
}