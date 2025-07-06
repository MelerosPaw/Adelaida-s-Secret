package com.example.composetest.data.mapper.fromdbo

import com.example.composetest.data.db.relations.TableroCompleto
import com.example.composetest.model.Casilla
import com.example.composetest.model.Tablero

fun TableroCompleto.toModel(): Tablero {
    val casillas: Array<Casilla> = casillas.map { it.toModel() }.toTypedArray()

    val tablero = Tablero(
        casillas, emptyArray(), Tablero.Constructor.habitaciones,
        tablero.cantidadFilas, tablero.cantidadColumnas
    )

    return tablero
}