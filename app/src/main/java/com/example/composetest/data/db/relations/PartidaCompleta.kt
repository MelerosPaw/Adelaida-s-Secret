package com.example.composetest.data.db.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.composetest.data.db.dbo.COLUMNA_PARTIDA
import com.example.composetest.data.db.dbo.COLUMNA__ID_EVENTO
import com.example.composetest.data.db.dbo.COLUMNA__ID_PARTIDA
import com.example.composetest.data.db.dbo.COLUMNA__ID_SOSPECHOSO
import com.example.composetest.data.db.dbo.EventoDBO
import com.example.composetest.data.db.dbo.JugadorDBO
import com.example.composetest.data.db.dbo.PartidaDBO
import com.example.composetest.data.db.dbo.SospechosoDBO
import com.example.composetest.data.db.dbo.TableroDBO
import com.example.composetest.data.db.relations.nm.EventoPartidaDBO
import com.example.composetest.data.db.relations.nm.SospechosoCompleto

class PartidaCompleta(
    @Embedded val partida: PartidaDBO,
    @Relation(
        parentColumn = COLUMNA__ID_PARTIDA,
        entityColumn = COLUMNA_PARTIDA,
        entity = JugadorDBO::class
    )
    val jugadores: List<JugadorCompleto>,
    @Relation(
        parentColumn = COLUMNA__ID_PARTIDA,
        entityColumn = COLUMNA__ID_PARTIDA,
        entity = TableroDBO::class
    )
    val tablero: TableroCompleto?,
    @Relation(
        parentColumn = COLUMNA__ID_SOSPECHOSO,
        entityColumn = COLUMNA__ID_SOSPECHOSO,
        entity = SospechosoDBO::class
    )
    val asesino: SospechosoCompleto?,
    @Relation(
        parentColumn = COLUMNA__ID_EVENTO,
        entityColumn = COLUMNA__ID_EVENTO,
    )
    val eventoActual: EventoDBO?,
    @Relation(
        parentColumn = COLUMNA__ID_PARTIDA,
        entityColumn = COLUMNA__ID_EVENTO,
        associateBy = Junction(EventoPartidaDBO::class)
    )
    val eventosConsumidos: List<EventoDBO>,
)