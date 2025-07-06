package com.example.composetest.ui.contracts

interface MVIIntencion

interface MVIConsumidor<I: MVIIntencion> {

  fun consumir(vararg intenciones: I)
}