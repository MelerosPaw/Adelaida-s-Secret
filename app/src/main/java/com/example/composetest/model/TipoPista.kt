package com.example.composetest.model

/**
 * @property reglasBaremo Reglas que determinan la puntuación que puede tener ese tipo de carta en el Baremo de fuerza
 * de la acusación.
 */
sealed class TipoPista(
    val nombre: String,
    private val reglasBaremo: ReglasBaremo
) {

    val posicionEnBaremo: Int
        get() = reglasBaremo.posicionEnBaremo

    class Habito : TipoPista("Hábito", ReglasBaremo(0, 2..6))
    class Objeto : TipoPista("Objeto", ReglasBaremo(1, 3..6))
    class Testigo : TipoPista("Testigo", ReglasBaremo(2, 2..6))
    class Coartada : TipoPista("Coartada", ReglasBaremo(3, 3..6))
    class Secreto : TipoPista("Secreto", ReglasBaremo(4, 3..5))

    fun estaFueraDeRango(valor: Int): Boolean = reglasBaremo.estaFueraDeRango(valor)
}