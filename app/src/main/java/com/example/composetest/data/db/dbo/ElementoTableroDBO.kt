package com.example.composetest.data.db.dbo

import androidx.room.ColumnInfo
import com.example.composetest.model.ElementoTablero

const val COLUMNA__ID_ELEMENTO = "idElemento"

/**
 *
 * @property idJugador Si es nulo, que no lo tenga ningún jugador significa que está en el tablero.
 * @property monedas Solo las cartas de dinero las pistas tienen monedas.
 * @property idCasilla La coordenada de la casilla (p.ej., B3).
 * @property texto Solo las pistas tiene texto.
 */
open class ElementoTableroDBO(
    val prefijo: String,
    val valor: String?,
    var monedas: Int? = null,
    @ColumnInfo(name = COLUMNA_PARTIDA) val partida: Long,
    @ColumnInfo(name = COLUMNA__ID_JUGADOR) val idJugador: Long? = null,
    @ColumnInfo(name = COLUMNA__ID_CASILLA) val idCasilla: String?,
    val gastado: Boolean = false,
    @ColumnInfo(name = COLUMNA__ID_ELEMENTO) val idElemento: String = prefijo + valor.orEmpty(),
) {

    companion object {

        fun crearId(prefijo: ElementoTablero.Prefijo, valor: String?) =
            crearId(prefijo.id, valor.orEmpty())

        fun crearId(prefijo: String, valor: String?) = prefijo + valor.orEmpty()
    }

    override fun toString(): String = idElemento
}