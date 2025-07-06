package com.example.composetest.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.model.Tablero
import com.example.composetest.ui.compose.sampledata.jugadores
import com.example.composetest.ui.compose.sampledata.tablero
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.viewmodel.TableroViewModel

/**
 * @param elementosFueraDelTablero Aquellos elementos que están en la mano de los jugadores.
 */
@Composable
fun TabTablero(
    tablero: Tablero?,
    elementosFueraDelTablero: List<ElementoTablero>?,
    idPartida: Long?,
    jugadores: List<Jugador>?,
    filtroAbierto: Boolean = false,
    onFiltrosDismissed: () -> Unit,
    onAccionProhibida: (AccionProhibida) -> Unit
) {
    if (tablero == null || elementosFueraDelTablero == null || idPartida == null) {
        Box(Modifier.fillMaxSize(), propagateMinConstraints = true) {
            Cargando()
        }

    } else {
        val tableroViewModel: TableroViewModel = hiltViewModel()
        tableroViewModel.inicializar(idPartida, tablero, elementosFueraDelTablero, true, jugadores)
        TableroConControles(jugadores, false, false, false,
            Modo.Dialogo(filtroAbierto, onFiltrosDismissed), onAccionProhibida)
    }
}

@NightAndDay
@Composable
fun PreviewTabTablero() {
    ScreenPreviewMarron {
        TabTablero(tablero(), emptyList(), 7L, jugadores(), false, {}, {})
    }
}