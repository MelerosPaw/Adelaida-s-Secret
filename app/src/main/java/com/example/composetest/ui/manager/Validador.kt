package com.example.composetest.ui.manager

interface Validacion {

  /** Condición a verificar. */
  fun validar(): Boolean
}

fun List<Validacion>.run(): Boolean = all { it.validar() }
