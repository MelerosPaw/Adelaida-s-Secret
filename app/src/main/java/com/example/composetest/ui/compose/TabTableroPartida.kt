package com.example.composetest.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.composetest.model.Jugador
import com.example.composetest.ui.viewmodel.MediodiaTardeViewModel.EstadoTablero

@Composable
fun TabTableroPartida(
  filtrosAbiertos: State<Boolean>,
  tablero: EstadoTablero?,
  jugadores: List<Jugador>?,
  idPartida: Long?,
  cerrarFiltros: () -> Unit,
  onAccionProhibida: (AccionProhibida) -> Unit,
) {
  val filtrosAbiertos by remember { filtrosAbiertos }

  TabTablero(
    tablero?.tablero, tablero?.elementosFueraDelTablero, idPartida, jugadores, filtrosAbiertos,
    { cerrarFiltros() }, onAccionProhibida)
}