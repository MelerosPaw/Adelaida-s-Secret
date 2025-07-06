package com.example.composetest.data.db.reduced

/**
 *
 * @property id El id de la carta.
 * @property partida El id de la partida.
 * @property gastado Pues si está gastada o no.
 */
class CartaGastada(
    val idElemento: String,
    val partida: Long,
    val gastado: Boolean
)
