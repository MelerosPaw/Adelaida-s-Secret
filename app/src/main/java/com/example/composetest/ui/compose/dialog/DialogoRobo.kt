package com.example.composetest.ui.compose.dialog

import androidx.compose.runtime.Composable
import com.example.composetest.model.ElementoTablero
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.manager.GestorRobo
import com.example.composetest.ui.manager.GestorRobo.OpcionRobo

@Composable
fun DialogoRobo(
    accionRobo: GestorRobo.AccionRobo,
    robarDeLaVitrina: () -> Unit,
    robarDelaMano: () -> Unit,
    cerrarDialogo: () -> Unit,
) {
    when (accionRobo.opcion) {
        is OpcionRobo.NadaDisponible -> NadaQueRobar(cerrarDialogo)
        is OpcionRobo.Robar -> Robar(accionRobo.opcion.hayPistas, accionRobo.opcion.hayCartas,
            robarDeLaVitrina, robarDelaMano, cerrarDialogo)
    }
}

@Composable
private fun NadaQueRobar(cerrarDialogo: () -> Unit) {
    AdelaidaButtonDialog(
        "Ahora mismo no merece la pena emborrachar a nadie.",
        arrayOf(OpcionDialogo("Pues me guardo la botella!", null) { cerrarDialogo() }),
        ElementoTablero.Carta.Brandy(1)
    )
}

@Composable
private fun Robar(
    hayPistas: Boolean,
    hayCartas: Boolean,
    robarVitrina: () -> Unit,
    robarMano: () -> Unit,
    cerrarDialogo: () -> Unit
) {
    val opcionVitrina = if (hayPistas) {
        OpcionDialogo("... una pista de su vitrina.", null, true) { robarVitrina() }
    } else {
        OpcionDialogo("... una pista no porque nadie tiene.", null, false) {}
    }

    val opcionMano = if (hayCartas) {
        OpcionDialogo("... una carta de la mano.", null, true) { robarMano() }
    } else {
        OpcionDialogo("... una carta no porque nadie tiene.", null, false) {}
    }

    AdelaidaButtonDialog(
        "Emborrachar a alguien para robarle...",
        arrayOf(
            opcionVitrina, opcionMano,
            OpcionDialogo("... nada; cerramos la botella.", null) { cerrarDialogo() },
        ),
        ElementoTablero.Carta.Brandy(1)
    )
}