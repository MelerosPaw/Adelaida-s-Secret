package com.example.composetest.model

/** Devuelve true si cada valor está dentro del rango permitido para esa pista. */
private fun Array<Int>.todosLosValoresEstanDentroDeRango(tipos: Array<TipoPista>): Boolean =
    tipos.none {
        val valorDeEsaPistaEnElBaremo = get(it.posicionEnBaremo)
        it.estaFueraDeRango(valorDeEsaPistaEnElBaremo)
    }


/**
 *
 * @property posiblesBaremos Total de combinaciones posibles de baremo. Por defecto están todas las
 * permutaciones sin elementos repetidos de los elementos del 2 al 6 para los cinco tipos de pistas.
 */
class CreadorBaremos(
    private val posiblesBaremos: Array<Array<Int>> = arrayOf(
        arrayOf(2, 3, 4, 5, 6),
        arrayOf(2, 3, 4, 6, 5),
        arrayOf(2, 3, 5, 4, 6),
        arrayOf(2, 3, 5, 6, 4),
        arrayOf(2, 3, 6, 4, 5),
        arrayOf(2, 3, 6, 5, 4),
        arrayOf(2, 4, 3, 5, 6),
        arrayOf(2, 4, 3, 6, 5),
        arrayOf(2, 4, 5, 3, 6),
        arrayOf(2, 4, 5, 6, 3),
        arrayOf(2, 4, 6, 3, 5),
        arrayOf(2, 4, 6, 5, 3),
        arrayOf(2, 5, 3, 4, 6),
        arrayOf(2, 5, 3, 6, 4),
        arrayOf(2, 5, 4, 3, 6),
        arrayOf(2, 5, 4, 6, 3),
        arrayOf(2, 5, 6, 3, 4),
        arrayOf(2, 5, 6, 4, 3),
        arrayOf(2, 6, 3, 4, 5),
        arrayOf(2, 6, 3, 5, 4),
        arrayOf(2, 6, 4, 3, 5),
        arrayOf(2, 6, 4, 5, 3),
        arrayOf(2, 6, 5, 3, 4),
        arrayOf(2, 6, 5, 4, 3),
        arrayOf(3, 2, 4, 5, 6),
        arrayOf(3, 2, 4, 6, 5),
        arrayOf(3, 2, 5, 4, 6),
        arrayOf(3, 2, 5, 6, 4),
        arrayOf(3, 2, 6, 4, 5),
        arrayOf(3, 2, 6, 5, 4),
        arrayOf(3, 4, 2, 5, 6),
        arrayOf(3, 4, 2, 6, 5),
        arrayOf(3, 4, 5, 2, 6),
        arrayOf(3, 4, 5, 6, 2),
        arrayOf(3, 4, 6, 2, 5),
        arrayOf(3, 4, 6, 5, 2),
        arrayOf(3, 5, 2, 4, 6),
        arrayOf(3, 5, 2, 6, 4),
        arrayOf(3, 5, 4, 2, 6),
        arrayOf(3, 5, 4, 6, 2),
        arrayOf(3, 5, 6, 2, 4),
        arrayOf(3, 5, 6, 4, 2),
        arrayOf(3, 6, 2, 4, 5),
        arrayOf(3, 6, 2, 5, 4),
        arrayOf(3, 6, 4, 2, 5),
        arrayOf(3, 6, 4, 5, 2),
        arrayOf(3, 6, 5, 2, 4),
        arrayOf(3, 6, 5, 4, 2),
        arrayOf(4, 2, 3, 5, 6),
        arrayOf(4, 2, 3, 6, 5),
        arrayOf(4, 2, 5, 3, 6),
        arrayOf(4, 2, 5, 6, 3),
        arrayOf(4, 2, 6, 3, 5),
        arrayOf(4, 2, 6, 5, 3),
        arrayOf(4, 3, 2, 5, 6),
        arrayOf(4, 3, 2, 6, 5),
        arrayOf(4, 3, 5, 2, 6),
        arrayOf(4, 3, 5, 6, 2),
        arrayOf(4, 3, 6, 2, 5),
        arrayOf(4, 3, 6, 5, 2),
        arrayOf(4, 5, 2, 3, 6),
        arrayOf(4, 5, 2, 6, 3),
        arrayOf(4, 5, 3, 2, 6),
        arrayOf(4, 5, 3, 6, 2),
        arrayOf(4, 5, 6, 2, 3),
        arrayOf(4, 5, 6, 3, 2),
        arrayOf(4, 6, 2, 3, 5),
        arrayOf(4, 6, 2, 5, 3),
        arrayOf(4, 6, 3, 2, 5),
        arrayOf(4, 6, 3, 5, 2),
        arrayOf(4, 6, 5, 2, 3),
        arrayOf(4, 6, 5, 3, 2),
        arrayOf(5, 2, 3, 4, 6),
        arrayOf(5, 2, 3, 6, 4),
        arrayOf(5, 2, 4, 3, 6),
        arrayOf(5, 2, 4, 6, 3),
        arrayOf(5, 2, 6, 3, 4),
        arrayOf(5, 2, 6, 4, 3),
        arrayOf(5, 3, 2, 4, 6),
        arrayOf(5, 3, 2, 6, 4),
        arrayOf(5, 3, 4, 2, 6),
        arrayOf(5, 3, 4, 6, 2),
        arrayOf(5, 3, 6, 2, 4),
        arrayOf(5, 3, 6, 4, 2),
        arrayOf(5, 4, 2, 3, 6),
        arrayOf(5, 4, 2, 6, 3),
        arrayOf(5, 4, 3, 2, 6),
        arrayOf(5, 4, 3, 6, 2),
        arrayOf(5, 4, 6, 2, 3),
        arrayOf(5, 4, 6, 3, 2),
        arrayOf(5, 6, 2, 3, 4),
        arrayOf(5, 6, 2, 4, 3),
        arrayOf(5, 6, 3, 2, 4),
        arrayOf(5, 6, 3, 4, 2),
        arrayOf(5, 6, 4, 2, 3),
        arrayOf(5, 6, 4, 3, 2),
        arrayOf(6, 2, 3, 4, 5),
        arrayOf(6, 2, 3, 5, 4),
        arrayOf(6, 2, 4, 3, 5),
        arrayOf(6, 2, 4, 5, 3),
        arrayOf(6, 2, 5, 3, 4),
        arrayOf(6, 2, 5, 4, 3),
        arrayOf(6, 3, 2, 4, 5),
        arrayOf(6, 3, 2, 5, 4),
        arrayOf(6, 3, 4, 2, 5),
        arrayOf(6, 3, 4, 5, 2),
        arrayOf(6, 3, 5, 2, 4),
        arrayOf(6, 3, 5, 4, 2),
        arrayOf(6, 4, 2, 3, 5),
        arrayOf(6, 4, 2, 5, 3),
        arrayOf(6, 4, 3, 2, 5),
        arrayOf(6, 4, 3, 5, 2),
        arrayOf(6, 4, 5, 2, 3),
        arrayOf(6, 4, 5, 3, 2),
        arrayOf(6, 5, 2, 3, 4),
        arrayOf(6, 5, 2, 4, 3),
        arrayOf(6, 5, 3, 2, 4),
        arrayOf(6, 5, 3, 4, 2),
        arrayOf(6, 5, 4, 2, 3),
        arrayOf(6, 5, 4, 3, 2),
    ),
    val tipos: Array<TipoPista> = arrayOf(
        TipoPista.Habito(),
        TipoPista.Objeto(),
        TipoPista.Testigo(),
        TipoPista.Coartada(),
        TipoPista.Secreto(),
    )
) {

    /** Recibe todos los posibles baremos y filtra según las reglas permitidas por tipo de pista. */
    fun crearBaremosRusticos() {
        val resultado = crearBaremos()
        val listado = resultado.joinToString("\n") { "${it.id} - ${it.valores.joinToString()}" }

        println("Conjuntos resultantes: ${resultado.size}\n$listado")
        println("\n--- Estadísticas")

        tipos.forEach { tipo ->
            println("Las pistas de tipo ${tipo.nombre} aparecen:")
            contarOcurrenciasDeTipoYValor(resultado, tipo, 6)
            contarOcurrenciasDeTipoYValor(resultado, tipo, 5)
            contarOcurrenciasDeTipoYValor(resultado, tipo, 4)
            contarOcurrenciasDeTipoYValor(resultado, tipo, 3)
            contarOcurrenciasDeTipoYValor(resultado, tipo, 2)
        }
    }

    fun crearBaremos(): List<Baremo> =
        posiblesBaremos
            .eliminarFueraDeRango(tipos)
            .mapIndexed { index, array -> Baremo("B${index.inc()}", array) }

    private fun contarOcurrenciasDeTipoYValor(resultado: List<Baremo>, tipo: TipoPista, valor: Int) {
        resultado.count { it.valores[tipo.posicionEnBaremo] == valor }
            .takeIf { it > 0 }
            ?.let {
                println("  - en $it baremos con el valor $valor")
            }
    }

    private fun Array<Array<Int>>.eliminarFueraDeRango(tipos: Array<TipoPista>): Array<Array<Int>> = this
        .filter { it.todosLosValoresEstanDentroDeRango(tipos) }
        .toTypedArray()

}