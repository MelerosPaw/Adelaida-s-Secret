package com.example.composetest.data.db.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val COLUMNA__ID_PARTIDA = "id"

@Entity
class PartidaDBO(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMNA__ID_PARTIDA) val id: Long,
    val nombre: String? = null,
    @ColumnInfo(name = COLUMNA__ID_SOSPECHOSO)
    val asesino: Long? = null,
    val fecha: String,
    val idEstado: String,
    val idRonda: String,
    val dia: Int,
    val fuerzaDefensa: Int,
    @ColumnInfo(name = COLUMNA__ID_EVENTO)
    val idEventoActual: Long? = null,
    val eventoActualEjecutado: Boolean = false,
)