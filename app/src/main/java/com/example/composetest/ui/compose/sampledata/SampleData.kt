package com.example.composetest.ui.compose.sampledata

import androidx.compose.runtime.mutableStateListOf
import com.example.composetest.model.Baremo
import com.example.composetest.model.Casilla
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Evento
import com.example.composetest.model.Habitacion
import com.example.composetest.model.Jugador
import com.example.composetest.model.Partida
import com.example.composetest.model.Rasgo
import com.example.composetest.model.Secreto
import com.example.composetest.model.Sospechoso
import com.example.composetest.model.Sospechoso.Genero
import com.example.composetest.model.Tablero
import com.example.composetest.model.elementosJuego
import com.example.composetest.ui.compose.widget.EventoVO
import kotlin.random.Random

// region Partida

fun partidas(cuantas: Int): List<Partida> = if (cuantas > 0) {
    listaLlena(cuantas)
} else {
    emptyList()
}

private fun listaLlena(cuantas: Int) = Array(cuantas) {
    Partida(
        it.toLong(),
        Partida.EstadoCreacion.PARTIDA_EMPEZADA,
        Partida.Ronda.NOCHE,
        7,
        jugadores().toTypedArray(),
        nombre = "Partida to wapa y homosesual ${it + 1}"
    )
}.toList()

// endregion

// region Jugador

fun nombres(): Array<String> = arrayOf("Eufrasio", "Atanasio", "Romualdo", "Ataulfo", "Balaustrado")

fun jugadores(vararg nombres: String = nombres()): List<Jugador> =
    nombres.map {
        Jugador(
            it,
            cartas = cartas(),
            pistas = pistas(),
            200,
            "B${Random.nextInt(36) + 1}"
        )
    }

// endregion

fun cartas(): MutableList<ElementoTablero.Carta> = mutableListOf(
    ElementoTablero.Carta.Dinero(1, 1000),
    ElementoTablero.Carta.Dinero(2, 500, true),
    ElementoTablero.Carta.Brandy(2, true),
    ElementoTablero.Carta.Perseskud()
)

fun pistas(): MutableList<ElementoTablero.Pista> = mutableStateListOf(
    ElementoTablero.Pista.Habito(2),
    ElementoTablero.Pista.Objeto(1),
    ElementoTablero.Pista.Secreto(6),
)

fun sospechosos(cuantos: Int): List<Sospechoso> = Array<Sospechoso>(cuantos) {
    val numero = it + 1
    Sospechoso(
        nombre = "Sospechoso n.º $numero",
        rasgos = arrayOf(
            Rasgo("H$numero", "Esto no se debe ver", "Es tela de malo"),
            Rasgo("C$numero", "Esto no se debe ver", "Se porta mal"),
            Rasgo("O$numero", "Esto no se debe ver", "Tiene malas pulgas"),
        ),
        secreto = Secreto("S$numero", "S${numero + 1 }", "Esto no se debe ver", "Se porta mal en secreto. Cuando alguien " +
                "lo vio portándose mal, se lo dijo a todo el mundo y todo el mundo en su pueblo le " +
                "señaló con el dedo. Ya no podía ir a la panadería. El panadero decía que su pan " +
                "no estaba dispuesto a ser comido por alguien que se porta mal. Ni siquiera a ser " +
                "llevado en una bolsa por una persona así."),
        Genero.HOMBRE,
    )
}.toList()

fun baremos(cuantos: Int): List<Baremo> = Array(cuantos) {
    fun valorAleatorioDeBaremo(): Int = Random.nextInt(5) + 2

    Baremo(
        "B${it.inc()}", arrayOf(
            valorAleatorioDeBaremo(),
            valorAleatorioDeBaremo(),
            valorAleatorioDeBaremo(),
            valorAleatorioDeBaremo(),
            valorAleatorioDeBaremo()
        )
    )
}.toList()

fun eventos(cuantos: Int): List<Evento> = Array(cuantos) {
    Evento(Partida.Ronda.MANANA, "El mendigo maldito ${it.inc()}",
        "Hace ${it.inc()} días que el mendigo se volvió malo y sus pústulas apestan las calles. " +
                "No salgas o te condenarás a olerlas y vomitar.",
        "un 3",
        Evento.MaxGanadores.Nadie,
        Evento.Accion.AplicarEfecto(Evento.Efecto.DobleRobo)
    )
}.toList()

fun eventosVo(cuantos: Int): List<EventoVO> =
    eventos(cuantos).map { EventoVO(it, Random.nextBoolean(), true) }

fun tablero(): Tablero = Tablero(
    arrayOf(
        Casilla('A', 1, ElementoTablero.Carta.Dinero(0, 200)),
        Casilla('A', 2, ElementoTablero.Carta.Dinero(0, 300)),
    ),
    emptyArray(),
    arrayOf(
        Habitacion.Salon(),
        Habitacion.CuartoCostura(),
        Habitacion.Pasillo(),
        Habitacion.SalaCuadros(),
        Habitacion.Despacho(),
        Habitacion.GabineteEsoterico(),
        Habitacion.Dormitorio(),
        Habitacion.AntesalaDormitorio(),
        Habitacion.Dormitorio()
    ),
    8, 8
)