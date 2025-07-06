package com.example.composetest.data.db.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TableroDBO(
    @PrimaryKey @ColumnInfo(name = COLUMNA__ID_PARTIDA) val idPartida: Long,
    val cantidadColumnas: Int,
    val cantidadFilas: Int,
) {
    fun info(): String = "Filas: $cantidadFilas, Columnas: $cantidadColumnas - (idPartida) $idPartida"
}