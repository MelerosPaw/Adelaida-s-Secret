package com.example.composetest.ui.manager

fun interface Validacion {

  /** Condición a verificar. */
  fun validar(): Boolean
}

class Validador<V: Validacion>(validaciones: List<V>) {

  val validaciones: MutableList<V> = validaciones.toMutableList()
  val valida = validaciones.run()

  infix fun and(validacion: V): Validador<V> = apply {
    validaciones.add(validacion)
  }

  private fun List<Validacion>.run(): Boolean = all { it.validar() }
}
