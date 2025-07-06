package com.example.composetest.ui

import com.example.composetest.model.Casilla

class CasillaVO(
    val casilla: Casilla,
    val contenidoEstaEnElTablero: Boolean,
    val puedeMostrarse: Boolean,
    val puedeDevolverseAlTablero: Boolean,
)