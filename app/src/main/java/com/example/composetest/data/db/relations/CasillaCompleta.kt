package com.example.composetest.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.composetest.data.db.dbo.COLUMNA__ID_CASILLA
import com.example.composetest.data.db.dbo.CasillaDBO
import com.example.composetest.data.db.dbo.ElementoCartaDBO
import com.example.composetest.data.db.dbo.ElementoPistaDBO

class CasillaCompleta(
    @Embedded val casilla: CasillaDBO,
    @Relation(parentColumn = COLUMNA__ID_CASILLA, entityColumn = COLUMNA__ID_CASILLA)
    val pista: ElementoPistaDBO?,
    @Relation(parentColumn = COLUMNA__ID_CASILLA, entityColumn = COLUMNA__ID_CASILLA)
    val carta: ElementoCartaDBO?,
)