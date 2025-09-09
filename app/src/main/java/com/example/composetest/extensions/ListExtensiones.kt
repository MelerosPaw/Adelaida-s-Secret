package com.example.composetest.extensions

fun <T> Iterable<T>.plusIf(elemento: T, condicion: Boolean): Iterable<T> {
  elemento.takeIf { condicion }?.let(::plus)
  return this
}

fun <T> List<T>.joinToStringHumanReadable(transform: (T) -> String): String =
  joinToString("") { elemento ->
    val posicion = indexOf(elemento)
    val ultimaPosicion = size - 1
    val alFinal = when (posicion) {
      ultimaPosicion - 1 -> " y "
      ultimaPosicion -> ""
      else -> ", "
    }

    "${transform(elemento)}$alFinal"
  }

fun <T> Array<T>.joinToStringHumanReadable(transform: (T) -> String = { it.toString() }): String =
  toList().joinToStringHumanReadable(transform)

fun List<*>.hasAtLeast(howManyAtLeast: Int) : Boolean = size >= howManyAtLeast