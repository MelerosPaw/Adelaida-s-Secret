package com.example.composetest.ui.manager

/**
 * @property valido Condición a verificar.
 * @property mensaje Mensaje que se mostraría si la condición no es válida.
 */
class Validacion(val valido: Boolean, val mensaje: String?)

fun List<Validacion>.obtenerMensajeSiNoEsValido(): String? = this
  .mapNotNull { validacion -> validacion.mensaje?.takeIf { !validacion.valido } }
  .joinToString("\n") { "\t- $it" }
  .takeIf { it.isNotBlank() }

fun List<Validacion>.fold(): Validacion = if (all { it.valido }) {
  Validacion(true, null)
} else {
  Validacion(false, obtenerMensajeSiNoEsValido())
}