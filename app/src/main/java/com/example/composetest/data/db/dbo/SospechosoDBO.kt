package com.example.composetest.data.db.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val COLUMNA__ID_SOSPECHOSO = "idSospechoso"

@Entity
class SospechosoDBO(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMNA__ID_SOSPECHOSO) val id: Int,
    val nombre: String,
    val genero: String
)