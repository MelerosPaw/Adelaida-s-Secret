package com.example.composetest.data.db.dbo

import androidx.room.Entity

/**
 * No nos interesa guardar ninguna propiedad relacionada con su construcción en el tablero.
 */
@Entity(primaryKeys = [COLUMNA_PARTIDA, COLUMNA__ID_ELEMENTO])
class ElementoPistaDBO(
    prefijo: String,
    valor: String?,
    monedas: Int,
    partida: Long,
    idJugador: Long? = null,
    idCasilla: String? = null,
    gastado: Boolean = false, // No se usa, porque las pistas no se gastan, pero Room lo necesita.
    idElemento: String = "" // No se usa pero a Room le hace falta.
): ElementoTableroDBO(prefijo, valor, monedas, partida, idJugador, idCasilla) {

    fun info(): String = "$prefijo$valor, id: $idElemento - (idPartida) $partida"
}