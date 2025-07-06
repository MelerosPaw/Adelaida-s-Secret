package com.example.composetest.data.mapper.fromdbo

import com.example.composetest.data.db.relations.CasillaCompleta
import com.example.composetest.model.Casilla
import com.example.composetest.model.ElementoTablero

fun CasillaCompleta.toModel(): Casilla {
    val elemento: ElementoTablero? = pista?.toModel() ?: carta?.toModel()
    return Casilla(casilla.fila, casilla.columna, elemento)
}