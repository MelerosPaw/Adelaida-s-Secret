package com.example.composetest.ui.compose

import androidx.annotation.DrawableRes
import com.example.composetest.R
import com.example.composetest.model.ElementoTablero

@DrawableRes
fun ElementoTablero.getIconoDecorativo(): Int =
    when (this) {
        is ElementoTablero.Pista.Coartada -> R.drawable.ic_coartada
        is ElementoTablero.Pista.Habito -> R.drawable.ic_habito_grande
        is ElementoTablero.Pista.Objeto -> R.drawable.ic_objeto
        is ElementoTablero.Pista.Secreto -> R.drawable.ic_secreto
        is ElementoTablero.Pista.Testigo -> R.drawable.ic_testigo
        is ElementoTablero.Pista.PistaFalsa -> R.drawable.ic_habito_grande
        is ElementoTablero.Carta.AcusacionExtra -> R.drawable.ic_habito_grande
        is ElementoTablero.Carta.Brandy -> R.drawable.ic_habito_grande
        is ElementoTablero.Carta.Dinero -> R.drawable.ic_habito_grande
        is ElementoTablero.Carta.Llave -> R.drawable.ic_llave
        is ElementoTablero.Carta.Perseskud -> R.drawable.ic_habito_grande
        is ElementoTablero.Carta.Reputacion -> R.drawable.ic_habito_grande
}