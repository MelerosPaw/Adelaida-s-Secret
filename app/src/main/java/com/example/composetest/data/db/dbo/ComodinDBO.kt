package com.example.composetest.data.db.dbo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ComodinDBO(
    @PrimaryKey val id: String,
    val nombre: String,
    val explicacion: String,
    val automatico: Boolean,
    val idRondaConsumo: String,
    val idEfecto: String,
)