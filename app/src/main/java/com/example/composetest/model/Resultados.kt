package com.example.composetest.model

import kotlin.random.Random

const val placeholderNumero = "\$NUMERO\$"

enum class Resultados(val placehoder: String, val fraseFija: String, val puntuaciones: Array<String>) {
  NUMERO(placeholderNumero, "", arrayOf(placeholderNumero)),

  UN_RESULTADO_VARIOS_JUGADORES("\$RESULTADOS_UNO\$",
    "aquellos jugadores que saquen",
    arrayOf("un $placeholderNumero")
  ),

  UN_RESULTADO_UN_JUGADOR("\$RESULTADOS_UNO_JUGADORES_UNO\$",
    "aquel jugador que saque",
    arrayOf(
      "un $placeholderNumero",
      "la puntuación más alta",
      "la puntuación más baja"
    )
  ),

  DOS_RESULTADOS_VARIOS_JUGADORES("\$RESULTADOS_DOS\$",
    "aquellos jugadores que saquen",
    arrayOf(
      "un $placeholderNumero o un $placeholderNumero",
      "más de 4",
      "menos de 3",
    )
  ),

  DOS_RESULTADOS_DOS_JUGADORES("\$RESULTADOS_DOS_JUGADORES_DOS\$",
    "los dos jugadores que saquen",
    arrayOf(
      "la puntuación más baja",
      "la puntuación más alta",
    )
  ),

  TRES_RESULTADOS_VARIOS_JUGADORES("\$RESULTADOS_TRES\$",
    "aquellos jugadores que saquen",
    arrayOf(
      " un número par",
      "un número impar",
      "más de 3",
      "menos de 4",
      "un $placeholderNumero, un $placeholderNumero o un $placeholderNumero",
    )
  );

  fun formatear(texto: String): PuntuacionFormateada {
    val indiceFraseEscogida = Random.nextInt(0, puntuaciones.size)
    val puntuacionEscogida = puntuaciones[indiceFraseEscogida]
    val fraseEscogidaFormateada = puntuacionEscogida.replacePlaceholdersWithDieResults(placeholderNumero)
    return PuntuacionFormateada(texto.replace(placehoder, "$fraseFija $fraseEscogidaFormateada"), fraseEscogidaFormateada)
  }
}

class PuntuacionFormateada(val explicacionFormateada: String, val puntuaciones: String)

private fun String.replacePlaceholdersWithDieResults(placeholder: String): String {
  val occurrences = countOccurrencesOf(placeholder)
  val dieResults = getDistincDieRolls(occurrences)
  return replaceOccurrencesWithDieResult(occurrences, dieResults, placeholder)
}

private fun String.replaceOccurrencesWithDieResult(
  occurrences: Int,
  dieResults: List<String>,
  placeholder: String,
): String {
  var stringWithReplacements = this

  repeat(occurrences) {
    stringWithReplacements = stringWithReplacements.replaceFirst(placeholder, dieResults[it])
  }

  return stringWithReplacements
}

/**
 * Devuelve [occurrences] números entre el 1 y el 6.
 *
 * @param occurrences Si se pasa más de 6, se devolverán igualmente 6. Un dado no puede producir
 * más de 6 resultados distintos.
 */
fun getDistincDieRolls(occurrences: Int): List<String> = Array(6) {
  it.inc().toString()
}.toList()
  .shuffled()
  .take(occurrences)
  .sorted()


private fun String.countOccurrencesOf(token: String): Int {
  var startIndex = 0
  var occurrences = 0
  var occurrenceFound = true

  do {
    val indexOfOccurrence = indexOf(token, startIndex)
    occurrenceFound = indexOfOccurrence != -1

    if (occurrenceFound) {
      occurrences++
      startIndex = indexOfOccurrence + token.length
    }
  } while (occurrenceFound)

  return occurrences
}
