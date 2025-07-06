package com.example.composetest.data.db.relations.nm

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.composetest.data.db.dbo.COLUMNA__ID_CONTENIDO
import com.example.composetest.data.db.dbo.COLUMNA__ID_SOSPECHOSO

@Entity(primaryKeys = [COLUMNA__ID_SOSPECHOSO, COLUMNA__ID_CONTENIDO])
class SospechosoContenidoDBO(
    @ColumnInfo(name = COLUMNA__ID_SOSPECHOSO) val idSospechoso: Int,
    @ColumnInfo(name = COLUMNA__ID_CONTENIDO) val idPista: String,
)