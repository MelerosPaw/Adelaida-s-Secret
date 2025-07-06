package com.example.composetest.model

import com.example.composetest.model.ElementoTablero.Carta.Llave
import java.util.logging.Level
import java.util.logging.Logger

sealed class Habitacion(
    val nombre: String,
    var casillas: Set<Casilla>
) {

    val cantidadCasillas: Int
        get() = casillas.size

    fun vaciar() {
        casillas = casillas.onEach(Casilla::vaciar)
    }

    //region Clases
    class Salon : Habitacion(
        "Salón",
        setOf(
            Casilla('E', 1),
            Casilla('D', 2),
            Casilla('E', 2),
            Casilla('D', 3),
            Casilla('E', 3),
            Casilla('F', 3),
            Casilla('D', 4),
            Casilla('E', 4),
            Casilla('F', 4),
            Casilla('D', 5),
            Casilla('E', 5),
            Casilla('F', 5),
        )
    ) {

        // La pista falsa no puede estar en el salón porque es la primera habitación y sería un coñazo.
        override fun admiteEsteElemento(elemento: ElementoTablero): Boolean =
            (elemento as? ElementoTablero.Pista.PistaFalsa)?.esSecreto() != true
                    && super.admiteEsteElemento(elemento)
    }

    class CuartoCostura : Habitacion(
        "Cuarto de costura",
        setOf(
            Casilla('F', 1),
            Casilla('G', 1),
            Casilla('H', 1),
            Casilla('F', 2),
            Casilla('G', 2),
            Casilla('H', 2),
            Casilla('G', 3),
            Casilla('H', 3),
        )
    )

    class Pasillo : Habitacion(
        "Pasillo",
        setOf(
            Casilla('C', 3),
            Casilla('C', 4),
            Casilla('G', 4),
            Casilla('H', 4),
            Casilla('C', 5),
            Casilla('G', 5),
            Casilla('H', 5),
            Casilla('C', 6),
            Casilla('D', 6),
            Casilla('E', 6),
            Casilla('F', 6),
            Casilla('G', 6),
            Casilla('C', 7),
            Casilla('D', 7),
            Casilla('E', 7),
            Casilla('F', 7),
            Casilla('G', 7),
        )
    )

    class SalaCuadros : Habitacion(
        "Sala de cuadros",
        setOf(
            Casilla('H', 6),
            Casilla('H', 7),
            Casilla('C', 8),
            Casilla('D', 8),
            Casilla('E', 8),
            Casilla('F', 8),
            Casilla('G', 8),
            Casilla('H', 8),
        )
    )

    class Despacho : Habitacion(
        "Despacho",
        setOf(
            Casilla('A', 6),
            Casilla('B', 6),
            Casilla('A', 7),
            Casilla('B', 7),
            Casilla('A', 8),
            Casilla('B', 8),
        )
    ) {

        override fun admiteEsteElemento(elemento: ElementoTablero): Boolean {
            return elemento !is Llave && super.admiteEsteElemento(elemento)
        }
    }

    class GabineteEsoterico : Habitacion(
        "Gabinete esotérico",
        setOf(
            Casilla('A', 4),
            Casilla('B', 4),
            Casilla('A', 5),
            Casilla('B', 5),
        )
    ) {

        override fun admiteEsteElemento(elemento: ElementoTablero): Boolean =
            elemento !is Llave && super.admiteEsteElemento(elemento)
    }

    class Dormitorio : Habitacion(
        "Dormitorio",
        setOf(
            Casilla('A', 1),
            Casilla('B', 1),
            Casilla('A', 2),
            Casilla('B', 2),
            Casilla('A', 3),
            Casilla('B', 3),
        )
    ) {
        
        override fun admiteEsteElemento(elemento: ElementoTablero): Boolean =
            elemento !is Llave && super.admiteEsteElemento(elemento)
    }
    
    class AntesalaDormitorio : Habitacion(
        "Antesala del dormitorio",
        setOf(
            Casilla('C', 1),
            Casilla('D', 1),
            Casilla('C', 2),
        )
    )
    //endregion

    open fun puedeContener(elemento: ElementoTablero): Boolean = !estaLlena() && admiteEsteElemento(elemento)

    open fun admiteEsteElemento(elemento: ElementoTablero): Boolean {
        val cantidadPermitidaPorHabitacion = elemento.cantidadPermitidaPorHabitacion
        val casillasConEseTipoDeElemento = casillas.count { it.contiene(elemento) }

        return cantidadPermitidaPorHabitacion == null || casillasConEseTipoDeElemento < cantidadPermitidaPorHabitacion
    }

    fun estaLlena(): Boolean = casillas.all { it.estaOcupada() }

    fun contieneCasilla(casilla: Casilla): Boolean = casillas.any { it.esLaMismaPosicion(casilla) }

    /**
     * Pone un elemento en una casilla aleatoria. Baraja las casillas que no estén ocupadas y pone el elemento en la
     * primera.
     */
    fun poner(elemento: ElementoTablero): Casilla? {
        val casillaLibre = casillas
            .filter { !it.estaOcupada() }
            .shuffled()
            .firstOrNull()

        if (casillaLibre == null) {
            val nombreElemento = elemento::class.simpleName

            Logger.getGlobal().log(
                Level.SEVERE,
                "No se puede poner $nombreElemento ${elemento.id} en $nombre" +
                        " porque no tiene más casillas libres. Estas son las casillas y sus contenidos:\n" +
                        "${getContenido()}"
            )
        }

        return casillaLibre?.apply { poner(elemento) }
    }

    private fun getContenido(): CharSequence = casillas.joinToString("\n") { "\t- $it: ${it.contenido?.id}" }

    override fun toString(): String {
        val nombre = this::class.simpleName!!
        val cantidadLlenos = casillas.count { it.estaOcupada() }
        val textoLlenos = when {
            cantidadLlenos == casillas.size -> "todas las casillas llenas"
            cantidadLlenos == 1 -> "1 casilla ocupada"
            else -> "$cantidadLlenos casillas ocupadas"
        }
        return "$nombre ($textoLlenos)"
    }
}