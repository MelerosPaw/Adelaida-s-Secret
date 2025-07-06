package com.example.composetest.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.composetest.data.db.dbo.COLUMNA__ID_JUGADOR
import com.example.composetest.data.db.dbo.ElementoCartaDBO
import com.example.composetest.data.db.dbo.ElementoPistaDBO
import com.example.composetest.data.db.dbo.JugadorDBO

class JugadorCompleto(
    @Embedded val jugador: JugadorDBO,
    @Relation(parentColumn = COLUMNA__ID_JUGADOR, entityColumn = COLUMNA__ID_JUGADOR)
    val cartas: List<ElementoCartaDBO>,
    @Relation(parentColumn = COLUMNA__ID_JUGADOR, entityColumn = COLUMNA__ID_JUGADOR)
    val pistas: List<ElementoPistaDBO>,
)