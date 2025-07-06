package com.example.composetest.data.db.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val COLUMNA__ID_EVENTO = "idEvento"

@Entity
class EventoDBO(
    @PrimaryKey @ColumnInfo(name = COLUMNA__ID_EVENTO) val id: Int,
    val ronda: String,
    val nombre: String,
    val explicacion: String,
    val maxGanadores: String,
    val idAccion: String,
)