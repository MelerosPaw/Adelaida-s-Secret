package com.example.composetest.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.composetest.data.db.dbo.COLUMNA__ID_PARTIDA
import com.example.composetest.data.db.dbo.CasillaDBO
import com.example.composetest.data.db.dbo.TableroDBO

class TableroCompleto(
    @Embedded val tablero: TableroDBO,
    @Relation(
        parentColumn = COLUMNA__ID_PARTIDA,
        entityColumn = COLUMNA__ID_PARTIDA,
        entity = CasillaDBO::class,
    )
    val casillas: List<CasillaCompleta>,
)