package com.example.composetest.model

import com.example.composetest.extensions.hasAtLeast
import com.example.composetest.model.Evento.Comodin
import java.util.LinkedList

private const val PISTAS_MAXIMAS_EN_LA_VITRINA = 3

/**
 * @param idsSecretosReveladosRonda Durante cada ronda, cada vez que un jugador adquiera un secreto,
 * ya sea por robo o por reasignación, se añadirá su id a esta lista.
 *
 * Al llegar cada noche, se comparará con la lista [idsSecretosRevelados]. Si
 * [idsSecretosReveladosRonda] contuviese algún secreto que aún no estuviera en la lista
 * [idsSecretosRevelados], significaría que, durante esa ronda, ha adquirido un secreto que no
 * conocía, por lo que tiene que ser visitado por Adelaida.
 *
 * Tras la vista, se añadirán todos los secretos de [idsSecretosReveladosRonda] a [idsSecretosRevelados]
 * y se vaciará [idsSecretosReveladosRonda], de forma que en la siguiente ronda:
 *
 * * Si el jugador obtuviera un secreto nuevo, se añadirá a [idsSecretosReveladosRonda].
 * * Si el jugador perdiese un secreto que ya tenía, no se sacará nada de la lista porque en la
 * lista no estará.
 * * Si el jugador volviera a adquirir un secreto que ha perdido, no pasará nada, porque, cuando se
 * compare con [idsSecretosRevelados], este ya estará allí también, y no contará como secreto nuevo.
 *
 * Se sacará de la lista si se reasigna (porque ha sido un error), pero en caso de robo por asunto
 * turbio, no, porque igualmente el jugador ya habrá conocido el secreto.
 *
 * @param idsSecretosRevelados Secretos totales conocidos por el jugador. Tras una visita a Adelaida,
 * se vuelcan en esta lista todos los secretos que ha conocido en la ronda
 * ([idsSecretosReveladosRonda]).
 */
 // TODO Melero: 18/4/25 Falta limpiar y transferir al transcurrir el día.
 //  Esto hace falta para la visita de Adelaida.
data class Jugador(
    val nombre: String,
    private val cartas: MutableList<ElementoTablero.Carta> = LinkedList(),
    private val pistas: MutableList<ElementoTablero.Pista> = LinkedList(),
    val dinero: Int = 0,
    val idBaremo: String? = null,
    val idsSecretosRevelados: List<String> = LinkedList(),
    val idsSecretosReveladosRonda: List<String> = LinkedList(),
    val comodines: List<Comodin> = LinkedList(),
    val efectos: List<Evento.Efecto> = LinkedList(),
) {
    override fun toString(): String = "$nombre: ${cartas.size} cartas, ${pistas.size} pistas"

    fun darPista(pista: ElementoTablero.Pista): Boolean {
        if (pistas.size == PISTAS_MAXIMAS_EN_LA_VITRINA || pistas.any { it.esLaMisma(pista) }) {
            return false
        } else {
            pistas.add(pista)
            return true
        }
    }

    fun quitarPista(pista: ElementoTablero.Pista) {
        pistas.removeIf { it.esLaMisma(pista) }
    }

    fun desecharPistas() {
        pistas.clear()
    }

    fun tieneDemasiadasPistas(): Boolean = pistas.size > PISTAS_MAXIMAS_EN_LA_VITRINA

    fun darCarta(carta: ElementoTablero.Carta) {
        cartas.add(carta)
    }

    fun desecharCartas() {
        cartas.clear()
    }

    fun quitarCarta(carta: ElementoTablero.Carta) {
        cartas.remove(carta)
    }

    fun pistaSinUbicar(): ElementoTablero.Pista? = pistas.getOrNull(PISTAS_MAXIMAS_EN_LA_VITRINA)

    fun pistas(): List<ElementoTablero.Pista> = pistas

    fun cartas(): List<ElementoTablero.Carta> = cartas

    fun mano(): Array<ElementoTablero> = (pistas + (cartas - gastadas())).toTypedArray()

    fun manoSinDinero(): List<ElementoTablero> {
        val cartas = cartas.filterNot {
            it.es(ElementoTablero.Carta.Prefijo.DINERO) || it.estaGastada
        }

        return pistas + cartas
    }

    fun manoComprable(): Array<ElementoTablero> = mano().filter { it.esComprable }.toTypedArray()

    fun gastadas(): List<ElementoTablero> = cartas.filter { it.estaGastada }

    infix fun esElMismoQue(otroJugador: Jugador?): Boolean = nombre == otroJugador?.nombre

    infix fun noEsElMismoQue(otroJugador: Jugador?): Boolean = !(this esElMismoQue otroJugador)

    fun tienePistasPorLasQueAunNoHaSidoVisitado(): Boolean = pistas().any {
        it is ElementoTablero.Pista.Secreto && it.id !in idsSecretosRevelados
    }

    fun tieneSuficientesCartas(cuantasAlMenos: Int): Boolean = cartas().hasAtLeast(cuantasAlMenos)

    fun tieneCarta(carta: ElementoTablero.Carta): Boolean = cartas().any { it == carta }
}