package com.example.composetest.model

import com.example.composetest.model.ElementoTablero.Carta.Prefijo.entries
import com.example.composetest.model.ElementoTablero.Pista.Prefijo.entries

/**
 * La cartas que no tienen acciones si las clicas cuando están en la mano son la reputación y el
 * Perseskud, porque solo sirven en el juicio.
 */
private val cartasSinAcciones: Array<ElementoTablero.Prefijo> = arrayOf(
    ElementoTablero.Carta.Prefijo.DINERO,
    ElementoTablero.Carta.Prefijo.REPUTACION,
    ElementoTablero.Carta.Prefijo.PERSESKUD,
)

const val iconoDinero: String = "🪙"

/**
 * @property prefijo La primera letra del identificador del tipo de elemento.
 * @property valor Posición en la que fue creada.
 * @property cantidadPermitidaPorHabitacion Cuántas puede haber en cada habitación. `null` es sin límite.
 * @property id El código que está impreso en la pista.
 * @property codigoConIcono Igual que [id], pero con un icona.
 */
sealed class ElementoTablero(
    val prefijo: Prefijo,
    val valor: String?,
    val esComprable: Boolean,
    val icono: String? = null,
    var cantidadPermitidaPorHabitacion: Int? = null,
    val id: String = "${prefijo.id}${valor ?: ""}",
) {

    abstract fun sePuedeDevolverAlTablero(): Boolean

    open fun codigoConIcono(): String = "${icono ?: prefijo.id}${valor ?: ""}"

    fun sePuedeUsar(): Boolean = prefijo !in cartasSinAcciones

    fun es(prefijo: Prefijo): Boolean = this.prefijo == prefijo

    fun ElementoTablero.esSecretoFalso(): Boolean = this is Pista.PistaFalsa && this.esSecreto()

    interface Prefijo {
        val id: String
    }

    override fun equals(other: Any?): Boolean = (other as? ElementoTablero)?.let {
        it.prefijo == prefijo && it.valor == valor
    } == true

    open fun esElMismoTipo(otro: ElementoTablero): Boolean = this::class == otro::class
            || prefijo == otro.prefijo

    override fun toString(): String = "${this::class.simpleName!!} $id, " +
            "cantidadPermitidaPorHabitacion: $cantidadPermitidaPorHabitacion"

    sealed class Pista(
        prefijo: Prefijo,
        valor: String?,
        icono: String? = null,
        cantidadPorHabitacion: Int? = null,
        codigo: String = "${prefijo.id}${valor ?: ""}",
        val monedas: Int,
    ): ElementoTablero(prefijo, valor, PRECIOS.isNotEmpty(), icono, cantidadPorHabitacion, codigo) {
        
        companion object {
            val PRECIOS: Map<Prefijo, Int> = mapOf(
                Prefijo.HABITO to 500,
                Prefijo.OBJETO to 500,
                Prefijo.COARTADA to 500,
                Prefijo.TESTIGO to 500,
                Prefijo.SECRETO to 2000,
            )
            
            val PRECIO_MAS_BAJO: Int = PRECIOS.minBy { it.value }.value
        }

        enum class Prefijo(override val id: String): ElementoTablero.Prefijo {
            HABITO("H"),
            OBJETO("O"),
            TESTIGO("T"),
            COARTADA("C"),
            SECRETO("S");

            companion object {

                fun fromId(id: String): Prefijo? = entries.firstOrNull { it.id == id }
            }
        }

        override fun sePuedeDevolverAlTablero(): Boolean = true

        fun esLaMisma(otro: Pista): Boolean = id == otro.id

        abstract fun esDeRasgos(): Boolean

        /**
         * No queremos que todos los hábitos acaben en una habitación, así que permitiremos como
         * máximo 2.
         */
        class Habito(orden: Int, monedas: Int = MONEDAS_EN_PISTA) : Pista(Prefijo.HABITO,
            orden.toString(), "\uD83E\uDED6", 2, monedas = monedas) {

            override fun esDeRasgos(): Boolean = true
        }

        /**
         * No queremos que todos los objetos estén en el mismo sitio, pero siendo del tipo de pista
         * de las que más hay, no podemos limitar a un objeto por habitación porque peligraría que
         * no hubiera suficientes habitaciones para cumplir con el resto de restricciones. Se
         * limita a 3 por habitación.
         */
        class Objeto(orden: Int, monedas: Int = MONEDAS_EN_PISTA) : Pista(Prefijo.OBJETO,
            orden.toString(), "🏺", 3, monedas = monedas) {

            override fun esDeRasgos(): Boolean = true
        }

        /**
         * Como es de las que menos cartas hay, tampoco permitiremos que haya más de dos testigos
         * por habitación.
         */
        class Testigo(orden: Int, monedas: Int = MONEDAS_EN_PISTA) : Pista(Prefijo.TESTIGO,
            orden.toString(), "\uD83D\uDC64", 2, monedas = monedas) {

            override fun esDeRasgos(): Boolean = true
        }

        /**
         * No queremos que todas las coartadas estén en el mismo sitio, pero siendo del tipo de
         * pista que más hay, no podemos limitar a una coartada por habitación, ya que peligraría
         * que no hubiese suficientes habitaciones para cumplir con el resto de restricciones. Se
         * limita a 3 por habitación.
         */
        class Coartada(orden: Int, monedas: Int = MONEDAS_EN_PISTA) : Pista(Prefijo.COARTADA,
            orden.toString(), "⏰", 3, monedas = monedas) {

            override fun esDeRasgos(): Boolean = true
        }

        class Secreto(orden: Int, monedas: Int = MONEDAS_EN_PISTA) : Pista(Prefijo.SECRETO,
            orden.toString(), "\uD83D\uDCD8", 1, monedas = monedas) {

            override fun esDeRasgos(): Boolean = false
        }

        class PistaFalsa(
            prefijo: Prefijo,
            cantidadPorHabitacion: Int? = null,
            monedas: Int = MONEDAS_EN_PISTA_FALSA,
        ) : Pista(prefijo, ID, "\uD83C\uDF1A", cantidadPorHabitacion, monedas = monedas) {

            companion object {
                const val ID = "F"
            }

            override fun esDeRasgos(): Boolean = !esSecreto()

            override fun codigoConIcono(): String = "🌚${prefijo.id}"

            fun esSecreto(): Boolean = prefijo == Prefijo.SECRETO
        }
    }

    sealed class Carta(
        prefijo: Prefijo,
        valor: String?,
        icono: String? = null,
        cantidadPorHabitacion: Int? = null,
        codigo: String = "${prefijo.id}${valor ?: ""}",
        val estaGastada: Boolean = false,
    ): ElementoTablero(prefijo, valor, false, icono, cantidadPorHabitacion, codigo) {

        enum class Prefijo(override val id: String): ElementoTablero.Prefijo {
            LLAVE("K"),
            BRANDY("B"),
            DINERO("D"),
            ACUSACION_EXTRA("AE"),
            PERSESKUD("P"),
            REPUTACION("RPT");

            companion object {

                fun fromId(id: String): Prefijo? = entries.firstOrNull { it.id == id }
            }
        }

        override fun sePuedeDevolverAlTablero(): Boolean = false

        /** No habrá más de una llave en cada habitación. */
        class Llave(orden: Int, estaGastada: Boolean = false) : Carta(Prefijo.LLAVE,
            orden.toString(), "\uD83D\uDD11", 1, estaGastada = estaGastada)

        /** A lo sumo puede haber dos brandys por habitación. */
        class Brandy(orden: Int, estaGastada: Boolean = false) : Carta(Prefijo.BRANDY,
            orden.toString(), "\uD83C\uDF77", 2, estaGastada = estaGastada)

        /**
         * Sin restricciones. Ocuparán las casillas que hayan dejado libres los elementos con
         * restricciones.
         */
        class Dinero(orden: Int, val monedas: Int, estaGastada: Boolean = false)
            : Carta(Prefijo.DINERO, orden.toString(), iconoDinero, estaGastada = estaGastada)

        /**
         * Sin restricciones. Ocuparán las casillas que hayan dejado libres los elementos con
         * restricciones.
         */
        class AcusacionExtra(estaGastada: Boolean = false) : Carta(Prefijo.ACUSACION_EXTRA, null,
            "👉", estaGastada = estaGastada)

        /**
         * Sin restricciones. Ocuparán las casillas que hayan dejado libres los elementos con
         * restricciones.
         */
        class Perseskud : Carta(Prefijo.PERSESKUD, null, "\uD83D\uDD36")

        class Reputacion(owner: String): Carta(Prefijo.REPUTACION, owner, "🔍")
    }
}