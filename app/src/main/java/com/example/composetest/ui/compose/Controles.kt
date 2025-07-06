package com.example.composetest.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.ui.compose.widget.TextCheckbox
import com.example.composetest.ui.viewmodel.FiltrosViewModel

@Composable
fun Controles(
    hayCasillasVacias: Boolean,
    puedeMostrarEsquema: Boolean,
    puedeMostrarBotonCasillasVacias: Boolean = false
) {
    val viewModel: FiltrosViewModel = hiltViewModel()

    Column {
        MostrarIconos(viewModel)
        MostrarValores(viewModel)
        MostrarEsquema(viewModel, puedeMostrarEsquema)
        HabitacionesVacias(viewModel, hayCasillasVacias, puedeMostrarBotonCasillasVacias)
    }
}

@Composable
private fun MostrarIconos(viewModel: FiltrosViewModel) {
    val estado by remember { viewModel.estadoFiltros }

    TextCheckbox("Iconos", estado.usarIconos, viewModel::conIconos)
}

@Composable
private fun MostrarValores(viewModel: FiltrosViewModel) {
    val estado by remember { viewModel.estadoFiltros }

    TextCheckbox("Valores", estado.mostrarValores, { viewModel.conValores(it) })
}

@Composable
private fun MostrarEsquema(viewModel: FiltrosViewModel, puedeMostrarEsquema: Boolean) {
    if (puedeMostrarEsquema) {
        val estado by remember { viewModel.estadoFiltros }
        TextCheckbox("Esquema", estado.mostrarEsquema, viewModel::mostrarEsquema)
    }
}

@Composable
private fun HabitacionesVacias(
    viewModel: FiltrosViewModel,
    hayCasillasVacias: Boolean,
    puedeMostrarBotonCasillasVacias: Boolean
) {
    val estado by remember { viewModel.estadoFiltros }

    if (puedeMostrarBotonCasillasVacias && (hayCasillasVacias || estado.tieneFiltros())) {
        TextCheckbox(
            "Id en habitaciones vacías",
            estado.mostrarValorHabitacionSiLaCeldaQuedaVacia,
            viewModel::habitacionesVacias
        )
    }
}