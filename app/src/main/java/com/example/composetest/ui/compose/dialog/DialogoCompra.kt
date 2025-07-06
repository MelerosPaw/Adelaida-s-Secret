package com.example.composetest.ui.compose.dialog

import androidx.compose.runtime.Composable
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.model.iconoDinero
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.manager.GestorCompra
import com.example.composetest.ui.manager.GestorCompra.OpcionCompra

@Composable
fun DialogoCompra(
    accionCompra: GestorCompra.AccionCompra,
    comprarPista: () -> Unit,
    comprarSecreto: () -> Unit,
    cerrarDialogo: () -> Unit,
) {
    when (accionCompra.opcion) {
        is OpcionCompra.DineroInsuficiente -> DineroInsuficiente(accionCompra.comprador, cerrarDialogo)
        is OpcionCompra.NadaDisponible -> NadaQueComprar(accionCompra.comprador, cerrarDialogo)
        is OpcionCompra.Pista -> Comprar(accionCompra.opcion.hayPistas, false, false,
            accionCompra.comprador, comprarPista, comprarSecreto, cerrarDialogo)
        is OpcionCompra.PistaOSecreto -> Comprar(accionCompra.opcion.hayPistas, true,
            accionCompra.opcion.haySecretos, accionCompra.comprador, comprarPista, comprarSecreto, cerrarDialogo)
    }
}

@Composable
private fun DineroInsuficiente(comprador: Jugador, cerrarDialogo: () -> Unit) {
    AdelaidaButtonDialog(
        "No tienes dinero suficiente para comprar nada.",
        arrayOf(OpcionDialogo("¡Total! ¡Tampoco quería nada!", null) { cerrarDialogo() }),
        ElementoTablero.Carta.Dinero(1, comprador.dinero)
    )
}

@Composable
private fun NadaQueComprar(comprador: Jugador, cerrarDialogo: () -> Unit) {
    AdelaidaButtonDialog(
        "Ahora mismo nadie tiene pistas para comprar.",
        arrayOf(OpcionDialogo("¡Qué se le va a hacer!", null) { cerrarDialogo() }),
        ElementoTablero.Carta.Dinero(1, comprador.dinero)
    )
}

@Composable
private fun Comprar(
    hayPistas: Boolean,
    leAlcanzaParaSecretos: Boolean,
    haySecretos: Boolean,
    comprador: Jugador,
    comprarPista: () -> Unit,
    comprarSecreto: () -> Unit,
    cerrarDialogo: () -> Unit
) {
    val opcionPistas = if (hayPistas) {
        OpcionDialogo("... una pista por 500 $iconoDinero.", null, true) { comprarPista() }
    } else {
        OpcionDialogo("... una pista no porque nadie tiene.", null, false) {}
    }

    val opcionSecretos = when {
        leAlcanzaParaSecretos && haySecretos ->
            OpcionDialogo("... un secreto por 2000 $iconoDinero.", null, true) { comprarSecreto() }
        leAlcanzaParaSecretos && !haySecretos ->
            OpcionDialogo("... un secreto no porque nadie tiene.", null, false) {}
        else ->
            OpcionDialogo("... un secreto no porque no te llega.", null, false) {}
    }

    AdelaidaButtonDialog(
        "Contratar a un sicario para robar...",
        arrayOf(
            opcionPistas, opcionSecretos,
            OpcionDialogo("... nada, me he arrepentido", null) { cerrarDialogo() },
        ),
        ElementoTablero.Carta.Dinero(1, comprador.dinero)
    )
}