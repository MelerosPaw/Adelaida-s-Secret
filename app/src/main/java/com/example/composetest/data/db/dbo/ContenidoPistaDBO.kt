package com.example.composetest.data.db.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val COLUMNA__ID_CONTENIDO = "idPista"

@Entity
class ContenidoPistaDBO(
    @PrimaryKey @ColumnInfo(name = COLUMNA__ID_CONTENIDO) val id: String,
    val idSecretoVinculado: String?,
    val texto: String,
    val textoEnLibro: String
)