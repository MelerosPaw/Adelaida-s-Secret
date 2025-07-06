package com.example.composetest.data.db.relations.nm

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.composetest.data.db.dbo.COLUMNA__ID_EVENTO
import com.example.composetest.data.db.dbo.COLUMNA__ID_PARTIDA

@Entity(primaryKeys = [COLUMNA__ID_PARTIDA, COLUMNA__ID_EVENTO])
class EventoPartidaDBO(
    @ColumnInfo(name = COLUMNA__ID_PARTIDA) val idPartida: Int,
    @ColumnInfo(name = COLUMNA__ID_EVENTO) val idEvento: Int,
)