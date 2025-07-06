package com.example.composetest.model

/**
 *
 * @property posicionEnBaremo Posición que ocupa en un conjunto de valores del baremo.
 * @property valoresPermitidos El valor mínimo y máximo que puede tener ese tipo de pista en el baremo.
 */
class ReglasBaremo(
    val posicionEnBaremo: Int,
    private val valoresPermitidos: IntRange
) {

    fun estaFueraDeRango(valor: Int): Boolean = valor !in valoresPermitidos
}