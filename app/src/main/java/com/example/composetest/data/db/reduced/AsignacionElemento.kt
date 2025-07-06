package com.example.composetest.data.db.reduced

import androidx.room.Ignore

data class AsignacionElemento(
    val prefijo: String,
    val valor: String?,
    val idElemento: String,
    val partida: Long,
    val idJugador: Long?,
    val monedas: Int?,
    @Ignore val idSecretoParaGuardar: String?,
    @Ignore val esDesasignacion: Boolean,
)