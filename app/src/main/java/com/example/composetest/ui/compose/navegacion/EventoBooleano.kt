package com.example.composetest.ui.compose.navegacion

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class EventoBooleano(
    private val valor: Boolean,
    private var handled: Boolean = false
): Parcelable {

    fun consume(): Boolean? = valor.takeIf { !handled }?.also { handled = true }
}