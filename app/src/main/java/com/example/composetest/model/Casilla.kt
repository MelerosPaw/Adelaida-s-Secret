package com.example.composetest.model

/**
 * @property contenido Es el identificador del elemento que hay en esta casilla. Si no hay, aún no está ocupada.
 */
class Casilla(
    val fila: Char,
    val columna: Int,
    var contenido: ElementoTablero? = null,
    var contenidoExtra: ElementoTablero? = null,
) {

    /**
     * No tiene en cuenta el contenido
     */
    fun esLaMismaPosicion(otra: Casilla): Boolean = otra.fila == fila && otra.columna == columna

    fun poner(elemento: ElementoTablero) {
        contenido = elemento
    }

    fun ponerExtra(elemento: ElementoTablero) {
        contenidoExtra = elemento
    }

    fun vaciar() {
        contenido = null
        contenidoExtra = null
    }

    fun estaOcupada(): Boolean = contenido != null

    fun contiene(elemento: ElementoTablero): Boolean = contenido?.esElMismoTipo(elemento) == true

    fun getFilaComoNumero(): Int = fila.aNumero()

    fun colindaPor(otraCasilla: Casilla): Colindacion = when {
        fila == otraCasilla.fila -> when {
            columna > otraCasilla.columna -> Colindacion.IZDA
            columna < otraCasilla.columna -> Colindacion.DCHA
            else -> Colindacion.NO
        }

        columna == otraCasilla.columna -> when {
            fila > otraCasilla.fila -> Colindacion.ARRIBA
            else -> Colindacion.ABAJO // Es la única condición posible porque ya se ha comprobado arriba que no
            // sean iguales
        }

        else -> Colindacion.NO
    }

    override fun toString(): String = "$fila$columna" + contenido?.toString()?.let {  " contiene $it" }.orEmpty()

    enum class Colindacion {
        ARRIBA, ABAJO, IZDA, DCHA, NO
    }
}