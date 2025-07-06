package com.example.composetest.ui.compose.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.sampledata.jugadores
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.viewmodel.DialogAsignarElementoViewModel

/**
 * @param existia Solo para depurar. Si la carta no existe, se guardará en la base de datos cuando
 * se le asigne al jugador en lugar de fallar la operación.
 * @param onDismiss El cuadro de diálogo tiene que cerrarse, por lo que tienes que cambiar el estado
 * que hace que se muestre para que no se abra inmediatamente. El booleano indica si el elemento
 * ha sido asignado o si hemos cerrado sin hacer ninguna acción (como al pulsar "Nadie") o cuando
 * se ha producido un error.
 */
@Composable
fun DialogoAsignarElemento(
    elemento: ElementoTablero,
    jugadores: List<Jugador>,
    poseedor: Jugador?,
    idPartida: Long,
    existia: Boolean = true,
    onDismiss: (Resultado) -> Unit,
) {
    val viewModel: DialogAsignarElementoViewModel = hiltViewModel()
    val opciones: Array<OpcionDialogo<Jugador?>> = viewModel.getOpcionesParaAsignar(
        jugadores, poseedor, elemento, existia,
        idPartida, onDismiss
    )

    AdelaidaButtonDialog("¿A quién?", opciones) { onDismiss(Resultado.Dismiss) }
}

@Preview
@Composable
fun Preview() {
    val jugadores = jugadores()
    ScreenPreviewMarron {
        DialogoAsignarElemento(
            ElementoTablero.Pista.Habito(32),
            jugadores,
            jugadores[0],
            30L,
        ) {}
    }
}