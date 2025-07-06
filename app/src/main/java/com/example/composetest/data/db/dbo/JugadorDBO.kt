package com.example.composetest.data.db.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val COLUMNA_NOMBRE = "nombre"
const val COLUMNA_PARTIDA = "partida"
const val COLUMNA__ID_JUGADOR = "idJugador"

@Entity
data class JugadorDBO(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMNA__ID_JUGADOR) val id: Long,
    @ColumnInfo(name = COLUMNA_NOMBRE) val nombre: String,
    @ColumnInfo(name = COLUMNA_PARTIDA) var partida: Long,
    val dinero: Int = 0,
    val baremo: String? = null,
    val idsSecretosConocidos: String? = null,
    val idsSecretosConocidosRonda: String? = null,
    val comodines: String? = null,
    val efectos: String? = null,
)