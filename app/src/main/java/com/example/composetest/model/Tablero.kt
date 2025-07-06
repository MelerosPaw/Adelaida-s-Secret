package com.example.composetest.model

import com.example.composetest.model.ElementoTablero.Carta.AcusacionExtra
import com.example.composetest.model.ElementoTablero.Carta.Brandy
import com.example.composetest.model.ElementoTablero.Carta.Dinero
import com.example.composetest.model.ElementoTablero.Carta.Llave
import com.example.composetest.model.ElementoTablero.Carta.Perseskud
import com.example.composetest.model.ElementoTablero.Pista.Coartada
import com.example.composetest.model.ElementoTablero.Pista.Habito
import com.example.composetest.model.ElementoTablero.Pista.Objeto
import com.example.composetest.model.ElementoTablero.Pista.PistaFalsa
import com.example.composetest.model.ElementoTablero.Pista.Secreto
import com.example.composetest.model.ElementoTablero.Pista.Testigo
import java.util.LinkedList
import java.util.logging.Level
import java.util.logging.Logger

private const val caracterPrevioAbecedario = 'A'.code - 1

fun Int.aCaracter(): Char = Char(caracterPrevioAbecedario + this)

fun Char.aNumero(): Int = code - caracterPrevioAbecedario

/**
 * Genera los secretos de forma desordenada, incluyendo el secreto falso entre ellos.
 *
 * Como hay 8 habitaciones y 9 secretos (uno de ellos, el falso), cuando se ponga el último secreto, siempre estará en
 * una habitación donde ya hay un secreto, por eso se barajan y al último secreto se le permite estar en una habitación
 * donde ya haya otro.
 *
 * No se puede poner el secreto falso siempre en último lugar (porque entonces siempre tendríamos un tablero en el que
 * el secreto falso estaría en la habitación donde hubiera dos secretos). Por eso barajamos los secretos.
 * Además, dado que el secreto falso no puede estar en el [Habitacion.Salon], no podemos permitir que el secreto falso se meta en
 * el tablero en penúltimo lugar, ya que para ese momento solo quedará una habitación libre y, si esta resulta ser el
 * [Habitacion.Salon], no se podrá colocar. Así que barajamos hasta que el penúltimo no sea el secreto falso.
 */
private fun producirSecretos(): Array<ElementoTablero.Pista> = Array(11) {
    if (it == 0) PistaFalsa(ElementoTablero.Pista.Prefijo.SECRETO, 1) else Secreto(it)
}.apply {
    shuffle()

//    while (last() is PistaFalsa) {
//        shuffle()
//    }
}

/**
 * Estos elementos son los que más restricciones de habitación tienen y están ordenados de más
 * restrictivos a menos restrictivos, por lo que no deben barajarse. Deben entrar en el tablero en
 * este orden (secretos, llaves y brandy) y ser los primeros para asegurarse de que siempre haya
 * casillas válidas para ellos cuando les toque colocarse.
 */
private fun producirRestrictivos(): Array<ElementoTablero> = arrayOf(
    *producirSecretos(),
    *Array(3) { Llave(it + 1) },
    *Array(5) { Brandy(it + 1) },
)

/**
 * Estos elementos son los que tiene menos restricciones de habitación, por lo que se pueden deben
 * barajar con otros sin restricciones para evitar que los que no tienen restricciones acaben
 * siempre en las mismas habitaciones restantes.
 */
private fun producirMenosRestrictivos(): Array<ElementoTablero> = arrayOf(
    *Array(5) { Habito(it + 1) },
    *Array(6) { Testigo(it + 1) },
    *Array(7) { Objeto(it + 1) },
    *Array(7) { Coartada(it + 1) },
    PistaFalsa(ElementoTablero.Pista.Prefijo.HABITO),
    PistaFalsa(ElementoTablero.Pista.Prefijo.OBJETO),
    PistaFalsa(ElementoTablero.Pista.Prefijo.TESTIGO),
    PistaFalsa(ElementoTablero.Pista.Prefijo.COARTADA),
    *Array(12) { Dinero(it + 1, 500) },
    *Array(7) { Dinero(it + 13, 1000) },
    *Array(1) { AcusacionExtra() },
    *Array(1) { Perseskud() },
).apply { shuffle() }

/**
 * Hay que poner primero los elementos que tienen constricciones de habitación, porque si no, puede que se hayan
 * llenado las habitaciones en las que deben ir para cuando les toque añadirse.
 */
val elementosJuego: Array<ElementoTablero>
    get() = arrayOf(
        *producirRestrictivos(),
        *producirMenosRestrictivos(),
    )

class Tablero(
    val casillas: Array<Casilla>,
    val separadores: Array<Array<Set<Casilla.Colindacion>>>,
    val habitaciones: Array<Habitacion>,
    val cantidadFilas: Int,
    val cantidadColumnas: Int,
) {

    private val casillasRellenas: Int
        get() = casillas.count { it.estaOcupada() }

    fun hayCasillasVacias(): Boolean = casillasRellenas < cantidadFilas * cantidadColumnas

    class Constructor(
        private val cantidadFilas: Int = 8,
        private val cantidadColumnas: Int = 8,
        private val elementos: Array<ElementoTablero> = elementosJuego,
    ) {

        companion object {

            /**
             * Esto solo sirve para que, una vez guardado el tablero, al recuperarlo de base de
             * datos, se puedan pintar las habitaciones de colores. La función
             * TableroViewModel.getColorDeCasilla recibe usa esas habitaciones para comprobar si una
             * casilla forma parte de una habitación o no.
             */
            val habitaciones: Array<Habitacion> = arrayOf(
                Habitacion.Dormitorio(), Habitacion.GabineteEsoterico(), Habitacion.Despacho(),
                Habitacion.AntesalaDormitorio(), Habitacion.Pasillo(), Habitacion.SalaCuadros(),
                Habitacion.Salon(),
                Habitacion.CuartoCostura(),
            )
        }

        val habitaciones: Array<Habitacion> = arrayOf(
            Habitacion.Dormitorio(), Habitacion.GabineteEsoterico(), Habitacion.Despacho(),
            Habitacion.AntesalaDormitorio(), Habitacion.Pasillo(), Habitacion.SalaCuadros(),
            Habitacion.Salon(),
            Habitacion.CuartoCostura(),
        )

        private val cantidadCasillas = cantidadFilas * cantidadColumnas
        private var reintentos: Int = 0

        fun construir() : Tablero = Tablero(
            crearCasillas(),
            crearSeparadores(),
            habitaciones,
            cantidadFilas,
            cantidadColumnas
        )

        private fun crearCasillas(): Array<Casilla> {
            imprimirAviso(cantidadCasillas)
            return rellenarCasillas(cantidadCasillas)
        }

        private fun crearSeparadores(): Array<Array<Set<Casilla.Colindacion>>> =
            Array(cantidadFilas + 1) { fila ->
                Array(cantidadColumnas + 1) { columna ->
                    when {
                        // La primera fila la queremos siempre con separadores entre números
                        fila == 0 -> setOf(Casilla.Colindacion.ABAJO, Casilla.Colindacion.DCHA)

                        // En el resto de filas...
                        // La primera, la de los números, solo se separará por la derecha
                        columna == 0 -> setOf(Casilla.Colindacion.ABAJO, Casilla.Colindacion.DCHA)

                        // Para las demás, hay que comprobar si colindan por la derecha y por abajo
                        else -> {
                            val casillaActual = Casilla(fila.aCaracter(), columna)
                            val habitacion = habitaciones.find { it.contieneCasilla(casillaActual) }
                            habitacion?.let {
                                setOf(
                                    colindaPorLaDerecha(casillaActual, habitacion),
                                    colindaPorAbajo(casillaActual, habitacion)
                                )
                            } ?: setOf(Casilla.Colindacion.NO)
                        }
                    }
                }
            }

        //region Casillas

        /**
         * Pone cada elemento en una casilla aleatoria de una habitación que permita contener ese
         * tipo de elemento.
         */
        private fun rellenarCasillas(cantidadCasillas: Int): Array<Casilla> {
            val elementosFinales = elementos.take(cantidadCasillas)
            val casillas: List<Casilla> = mapearElementosACasillas(elementosFinales)

            val todasLasCasillas = casillas.takeIf { it.size == cantidadCasillas }?.toTypedArray()

            return if (todasLasCasillas != null) {
                imprimirReintentos()
                todasLasCasillas
            } else {
                Logger.getGlobal().log(Level.SEVERE,
                    "Intento ${reintentos.inc()}: solo se han colocado ${casillas.size} casillas de $cantidadCasillas")
                reintentar(cantidadCasillas)
            }

        }

        private fun mapearElementosACasillas(elementosFinales: List<ElementoTablero>): List<Casilla> {
            val casillas: MutableList<Casilla> = LinkedList()
            for (elemento in elementosFinales) {
                val habitacion = encontrarHabitacionParaElemento(elemento)
                val casilla = habitacion?.poner(elemento)

                if (casilla == null) {
                    // Poner punto de ruptura aquí para saber cuál es el último elemento que se
                    // queda sin poderse meter, y comprobar por qué ninguna habitación lo admite.
                    informarSinHabitaciones(elemento)
                    break
                } else {
                    casillas.add(casilla)
                }
            }
            return casillas
        }

        private fun informarSinHabitaciones(elemento: ElementoTablero) {
            Logger.getGlobal().log(
                Level.SEVERE,
                "No había habitaciones disponibles para poner $elemento."
            )
        }

        private fun encontrarHabitacionParaElemento(elemento: ElementoTablero): Habitacion? = habitaciones
            .filter { it.puedeContener(elemento) }
            .shuffled()
            .firstOrNull()

        private fun reintentar(cantidadCasillas: Int): Array<Casilla> {
            reintentos++
            habitaciones.forEach(Habitacion::vaciar)
            return rellenarCasillas(cantidadCasillas)
        }

        private fun imprimirReintentos() {
            reintentos.takeIf { it > 0 }
                ?.let { Logger.getGlobal().log(Level.INFO, "Reintentos totales: $it") }
        }

        private fun imprimirAviso(cantidadCasillas: Int) {
            val cantidadElementos: Int = elementos.size
            val aviso = when {
                cantidadElementos > cantidadCasillas -> "Solo hay $cantidadCasillas casillas, pero hay " +
                        "$cantidadElementos elementos. El tablero se llenará con los $cantidadCasillas primeros."

                cantidadElementos == cantidadCasillas -> "Hay $cantidadElementos elementos."
                else -> "Solo hay $cantidadElementos casillas. " +
                        "${cantidadCasillas - cantidadElementos}/$cantidadCasillas " + "casillas se van a quedar vacías."
            }

            Logger.getGlobal().log(Level.INFO, aviso)
        }

        //endregion

        //region Separadores
        private fun colindaPorLaDerecha(casillaActual: Casilla, habitacion: Habitacion?): Casilla.Colindacion {
            val casillaASuDerecha: Casilla? = casillaActual.getCasillaALaDerecha()
            val habitacionCasillaDerecha: Habitacion? = getHabitacion(casillaASuDerecha)
            return casillaASuDerecha?.takeIf { habitacion == habitacionCasillaDerecha }
                ?.let { Casilla.Colindacion.DCHA }
                ?: Casilla.Colindacion.NO
        }

        private fun colindaPorAbajo(casillaActual: Casilla, habitacion: Habitacion?): Casilla.Colindacion {
            val casillaDebajo: Casilla? = casillaActual.getCasillaDebajo()
            val habitacionCasillaDebajo: Habitacion? = getHabitacion(casillaDebajo)
            return casillaDebajo?.takeIf { habitacion == habitacionCasillaDebajo }
                ?.let { Casilla.Colindacion.ABAJO }
                ?: Casilla.Colindacion.NO
        }

        private fun Casilla.getCasillaALaDerecha(): Casilla? =
            columna.inc().takeUnless { it > cantidadColumnas }?.let { Casilla(fila, it) }

        private fun Casilla.getCasillaDebajo(): Casilla? =
            fila.inc().takeUnless { it.aNumero() > cantidadFilas }?.let { Casilla(it, columna) }

        private fun getHabitacion(casillaASuDerecha: Casilla?): Habitacion? =
            casillaASuDerecha?.let { habitaciones.find { hab -> hab.contieneCasilla(it) } }
        //endregion
    }

    class Impresora(
        var tablero: Tablero,
        var mostrarIdHabitacionSiLaCeldaQuedaVacia: Boolean = false,
        var usarIconos: Boolean = false,
        var usarValores: Boolean = true,
        vararg filtros: ElementoTablero,
    ) {

        private val casillas: Array<Casilla> = tablero.casillas
        private val separadores: Array<Array<Set<Casilla.Colindacion>>> = tablero.separadores
        private val habitaciones: Array<Habitacion> = tablero.habitaciones
        private val cantidadFilas: Int = tablero.cantidadFilas
        private val cantidadColumnas: Int = tablero.cantidadColumnas
        val setFiltros: MutableSet<ElementoTablero> = mutableSetOf(*filtros)

        fun imprimir() {
            println(esquema())
        }

        fun esquema(): CharSequence {
            val casillasFiltradas: Array<Casilla> = filtrarCasillas()
            val casillasImpresas: Array<Array<String>> = pintarContenidoCeldas(casillasFiltradas)
            val separadoresHorizontales = separadores.mapIndexed { fila, array ->
                array.mapIndexed { numeroColumna, columna ->
                    if (columna.contains(Casilla.Colindacion.ABAJO)) {
                        if (numeroColumna == 0) {
                            "     "
                        } else {
                            "        "
                        }
                    } else {
                        "------- "
                    }
                }
            }

            val tablero = casillasImpresas.mapIndexed { numeroFila, fila ->
                val filaImpresa = fila.mapIndexed { numeroColumna, columna ->
                    if (numeroFila == 0 || numeroColumna == 0) {
                        "$columna|"
                    } else {
                        columna + ("|".takeUnless { separadores[numeroFila][numeroColumna].contains(
                            Casilla.Colindacion.DCHA) }
                            ?: " ")
                    }
                }.joinToString("")


                val lineaHorizontal = if (numeroFila == 0) {
                    "\n     ------- ------- ------- ------- ------- ------- ------- -------\n"
                } else {
                    separadoresHorizontales[numeroFila].joinToString("", prefix = "\n", postfix = "\n")
                }

                filaImpresa + lineaHorizontal
            }

            return tablero.joinToString("")
        }

        fun noEstaFiltrado(elemento: ElementoTablero): Boolean =
            setFiltros.isEmpty() || setFiltros.any { it.esElMismoTipo(elemento) }

        fun nuevoFiltro(filtro: ElementoTablero) {
            if (setFiltros.none { it.esElMismoTipo(filtro) }) {
                setFiltros.add(filtro)
            }
        }

        fun borrarFiltro(filtro: ElementoTablero) {
            setFiltros
                .firstOrNull(filtro::esElMismoTipo)
                ?.let(setFiltros::remove)
        }

        private fun filtrarCasillas(): Array<Casilla> =
            casillas.takeUnless { setFiltros.isNotEmpty() }
                ?: casillas.filter { casilla ->
                casilla.contenido == null || setFiltros.any { casilla.contenido?.esElMismoTipo(it) == true } }
                .toTypedArray()

        /** Crea un array bidimensional en el que pinta el contenido de cada celda. */
        private fun pintarContenidoCeldas(casillasRellenadas: Array<Casilla>): Array<Array<String>> =
            Array(cantidadFilas + 1) { fila ->
                Array(cantidadColumnas + 1) { columna ->
                    when {
                        // La esquina superior izquierda va sin contenido
                        columna == 0 && fila == 0 -> "    "

                        // La primera fila tiene el número de la columna
                        fila == 0 && columna > 0 -> generarCabeceraColumna(columna)

                        // La primera columna tiene el número de la fila (es una letra)
                        columna == 0 && fila > 0 -> generarCabeceraFila(fila)

                        // Las demás casillas son siempre casillas de habitaciones
                        else -> generarValorHabitacion(casillasRellenadas,
                            Casilla(fila.aCaracter(), columna)
                        )
                            ?: run {
                                if (mostrarIdHabitacionSiLaCeldaQuedaVacia) {
                                    getSiglasHabitacion(fila, columna) ?: formatearCelda("")
                                } else {
                                    formatearCelda("")
                                }
                            }
                    }
                }
            }

        private fun generarCabeceraColumna(columna: Int) = formatearCelda(columna.toString())

        private fun generarCabeceraFila(fila: Int): String = fila.aCaracter().let { letra -> " $letra  " }

        private fun generarValorHabitacion(casillas: Array<Casilla>, casilla: Casilla): String? =
            casillas
                .find { it.esLaMismaPosicion(casilla) }?.contenido
                ?.let { it.id.takeUnless { usarIconos } ?: it.codigoConIcono() }
                ?.let { formatearCelda(it) }

        fun getSiglasHabitacion(fila: Int, columna: Int): String? {
            val casilla = Casilla(fila.aCaracter(), columna)
            return getSiglasHabitacion(casilla)
        }

        /**
         * Devuelve las dos primeras letras de la sala en la que está la casilla o nulo si la
         * casilla no está en ninguna sala.
         */
        fun getSiglasHabitacion(casilla: Casilla): String? {
            val habitacion: Habitacion? = habitaciones.firstOrNull { it.contieneCasilla(casilla) }
            val idHabitacion: String? = habitacion?.toString()?.take(2)
            return idHabitacion?.let(::formatearCelda)
        }

        private fun formatearCelda(contenido: String): String {
            val longitudTotal = 7
            var resultado: String = contenido

            val caracteresFaltantes = longitudTotal - contenido.length
            val espaciosNecesarios = caracteresFaltantes / 2
            val esImpar = caracteresFaltantes % 2 == 1

            repeat(espaciosNecesarios) {
                resultado = " ".plus(resultado).plus(" ")
            }

            if (esImpar) {
                if (contenido.length == 1) {
                    resultado = resultado.plus(" ")
                } else {
                    resultado = " ".plus(resultado)
                }
            }

            return resultado
        }

        fun tieneFiltros(): Boolean = setFiltros.isNotEmpty()

        fun copiar(): Impresora = Impresora(
            tablero,
            mostrarIdHabitacionSiLaCeldaQuedaVacia,
            usarIconos,
            usarValores,
            *setFiltros.toTypedArray()
        )
    }
}