package com.example.composetest.ui.compose

import androidx.compose.runtime.Composable
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.ElementoTablero
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo

@Composable
fun DialogoAccionProhibida(estado: EstadoAccionProhibida?) {
    estado?.let {
        noneNull(it.advertencia, it.accionProhibida) { advertencia, accionProhibida ->
            AdelaidaButtonDialog(
                advertencia,
                arrayOf(
                    OpcionDialogo("Sí", null) { accionProhibida.onSePermite() },
                    OpcionDialogo("No", null) { accionProhibida.onNoSePermite() },
                ),
                onDismiss = accionProhibida.onNoSePermite
            )
        }
    }
}

class EstadoAccionProhibida(val advertencia: String?, val accionProhibida: AccionProhibida?)

sealed class PosibleAccionProhibida() {
    class Compra() : PosibleAccionProhibida()
    class Robo() : PosibleAccionProhibida()
    data class Reasignacion(val elemento: ElementoTablero, val tabActual: TabData) : PosibleAccionProhibida()
    class CambioTab(val pagina: TabData) : PosibleAccionProhibida()
}


class AccionProhibida(
    val posibleAccionProhibida: PosibleAccionProhibida,
    val onSePermite: () -> Unit,
    val onNoSePermite: () -> Unit
)
