package com.example.composetest.data.db.relations.nm

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.composetest.data.db.dbo.COLUMNA__ID_CONTENIDO
import com.example.composetest.data.db.dbo.COLUMNA__ID_SOSPECHOSO
import com.example.composetest.data.db.dbo.ContenidoPistaDBO
import com.example.composetest.data.db.dbo.SospechosoDBO

class SospechosoCompleto(
    @Embedded val sospechoso: SospechosoDBO,
    @Relation(
        parentColumn = COLUMNA__ID_SOSPECHOSO,
        entityColumn = COLUMNA__ID_CONTENIDO, // TODO Melero: 25/12/24 Esto muestra una advertencia al compilar
        associateBy = Junction(SospechosoContenidoDBO::class)
    )
    val pistas: List<ContenidoPistaDBO>,
)