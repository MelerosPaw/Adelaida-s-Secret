package com.example.composetest.ui.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.sampledata.jugadores
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.compose.widget.dialog.AdelaidaDialog
import com.example.composetest.ui.viewmodel.TabJugadoresViewModel

/**
 * @param onElementoClicado Reciben el elemento clicado y el dueño de dicho elemento.
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun Papelera(
    jugadoresConCartasGastadas: List<Jugador>,
    onCerrarPapelera: () -> Unit,
    onElementoClicado: (elementoClicado: ElementoTablero, poseedor: Jugador) -> Unit
) {
    AdelaidaDialog(onCerrarPapelera, contentMustScroll = false) {
        Box(Modifier.fillMaxWidth()) {
            Titulo("Papelera", nivel = NivelTitulo.Nivel2, textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center))
            Equis(onCerrarPapelera, "Cerrar", modifier = Modifier.align(Alignment.CenterEnd))
        }

        MatrizJugadores(
            null, // No vamos a permitir cambiar el nombre de un jugador desde aquí, así que no lo necesitamos
            jugadores = jugadoresConCartasGastadas,
            elementosAMostrar = TabJugadoresViewModel.MostrarElementos.Gastadas(),
            opcionesClicado = mutableStateOf(TabJugadoresViewModel.OpcionesClicado.Papelera()),
            configMarcadorDinero = TabJugadoresViewModel.ConfigMarcadorDinero(false, false),
            sePuedeClicarEsteElemento = { _, _ ->  true }, // Todos los elementos se pueden clicar
            onElementoClicado = { elemento, poseedor ->
                onElementoClicado(elemento, poseedor)
                onCerrarPapelera()
            },
            null,
            null,
            jugadorConCartasAbiertas = mutableStateOf(null),
            {},
            { _, _ -> },
            {},
            { _, _ -> },
            nivelTitulo = NivelTitulo.Nivel3,
        )
    }
}

@Composable
@Preview(name = "Papelera")
private fun P1() {
    ScreenPreviewMarron {
        Papelera(jugadores(), {}, { _, _ -> })
    }
}