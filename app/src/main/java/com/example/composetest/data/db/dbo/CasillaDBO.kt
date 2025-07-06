package com.example.composetest.data.db.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

private const val COLUMN__FILA = "fila"
private const val COLUMNA__COLUMNA = "columna"
const val COLUMNA__ID_CASILLA = "idCasilla"

/**
 *
 * @property idCasilla La combinación de la fila, la columna y el id de la partida es necesaria para
 * que sirva de clave ajena en [ElementoTableroDBO], ya que una @Relation solo puede hacerse
 * mediante una sola columna.
 */
@Entity
class CasillaDBO(
    @ColumnInfo(name = COLUMN__FILA) val fila: Char,
    @ColumnInfo(name = COLUMNA__COLUMNA) val columna: Int,
    @ColumnInfo(name = COLUMNA__ID_PARTIDA) val idPartida: Long,
    @PrimaryKey @ColumnInfo(name = COLUMNA__ID_CASILLA) val idCasilla: String =
        "$fila$columna$idPartida"
) {

    fun info(): String = "$fila, $columna - (idPartida) $idPartida"
}