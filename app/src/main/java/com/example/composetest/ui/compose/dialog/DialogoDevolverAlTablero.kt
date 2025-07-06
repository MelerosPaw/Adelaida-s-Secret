package com.example.composetest.ui.compose.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetest.R
import com.example.composetest.model.ElementoTablero
import com.example.composetest.ui.compose.ElementoMano
import com.example.composetest.ui.compose.sampledata.pistas
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.widget.AdelaidaIcon
import com.example.composetest.ui.compose.widget.AdelaidaIconDefaults
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.manager.GestorPistaPendiente

@Composable
fun DialogoPistaPendiente(
    pistaPendiente: ElementoTablero.Pista,
    accion: GestorPistaPendiente.Accion,
    onEjecutarAccion: (GestorPistaPendiente.Accion) -> Unit,
    cerrarDialogo: () -> Unit
) {
    AdelaidaButtonDialog(
        accion.getMensajeConfirmacion(pistaPendiente),
        arrayOf(
            OpcionDialogo("Sí", null) { onEjecutarAccion.invoke(accion) },
            OpcionDialogo("No", null) { cerrarDialogo() }
        ), { accion.cabecera.invoke(pistaPendiente) },
    )
}

@Composable
fun ContenedorDialogDevolverATablero(pistaPendiente: ElementoTablero.Pista) {
    ElementoConDestino(pistaPendiente, painterResource(R.drawable.ic_tablero))
}

@Composable
fun ContenedorDialogReemplazo(pistaPendiente: ElementoTablero.Pista, pistaAReemplazar: ElementoTablero.Pista) {
    Row {
        ElementoConDestino(pistaPendiente, Icons.Default.Check, Color.Green)
        ElementoConDestino(pistaAReemplazar, painterResource(R.drawable.ic_tablero))
    }
}

@Composable
private fun ElementoConDestino(
    pistaPendiente: ElementoTablero.Pista,
    icon: ImageVector,
    tint: Color = AdelaidaIconDefaults.tint,
) {
    Box {
        ElementoMano(pistaPendiente)
        AdelaidaIcon(icon, null, Modifier.size(48.dp).align(Alignment.TopEnd), tint)
    }
}

@Composable
private fun ElementoConDestino(
    pistaPendiente: ElementoTablero.Pista,
    icon: Painter,
    tint: Color = AdelaidaIconDefaults.tint,
) {
    Box {
        ElementoMano(pistaPendiente)
        AdelaidaIcon(icon, null, Modifier.size(48.dp).align(Alignment.TopEnd), tint)
    }
}

@Composable
@Preview(name = "Devolver al Tablero")
private fun P1() {
    ScreenPreviewMarron {
        DialogoPistaPendiente(
            pistaPendiente = pistas()[0],
            accion = GestorPistaPendiente.Accion.DevolucionATablero {},
            {},
            {}
        )
    }
}

@Composable
@Preview(name = "Reemplazar")
private fun P2() {
    ScreenPreviewMarron {
        DialogoPistaPendiente(
            pistaPendiente = pistas()[0],
            accion = GestorPistaPendiente.Accion.Reemplazo(pistas()[1]) { _, _, _, _ -> },
            {},
            {}
        )
    }
}
