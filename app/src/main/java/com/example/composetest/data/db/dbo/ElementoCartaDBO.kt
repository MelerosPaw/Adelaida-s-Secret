package com.example.composetest.data.db.dbo

import androidx.room.Entity

/**
 * Como se guardan en la mano del jugador, no nos interesa ninguna propiedad relacionada con su
 * construcción en el tablero.
 *
 * @property id El código de la carta, que será lo mismo que [ElementoTablero.Carta.id].
 */
@Entity(primaryKeys = [COLUMNA_PARTIDA, COLUMNA__ID_ELEMENTO])
class ElementoCartaDBO(
    prefijo: String,
    valor: String?,
    partida: Long,
    monedas: Int? = null,
    idJugador: Long? = null,
    idCasilla: String? = null,
    gastado: Boolean = false,
    idElemento: String = prefijo + valor.orEmpty()
) : ElementoTableroDBO(prefijo, valor, monedas, partida, idJugador, idCasilla, gastado, idElemento) {

    fun info(): String = "$prefijo$valor${monedas?.let { " ($it)" } ?: ""}, id: $idElemento - (idPartida) $partida"

    override fun toString(): String = super.toString() + monedas?.let { " - $it" }.orEmpty()
}