package com.example.composetest.ui.compose.screen

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.model.Sospechoso
import com.example.composetest.ui.compose.LibroSecretos
import com.example.composetest.ui.compose.navegacion.CLAVE_CARGA_INICIAL
import com.example.composetest.ui.compose.navegacion.NavegadorCreacion
import com.example.composetest.ui.compose.sampledata.sospechosos
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.NightAndDay
import com.example.composetest.ui.viewmodel.SospechososViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ScreenLibroSecretos() {
    val sospechososViewModel: SospechososViewModel = hiltViewModel()
    val ancho = calculateWindowSizeClass(LocalContext.current as Activity).widthSizeClass
    val columnas = 2.takeIf { ancho == WindowWidthSizeClass.Expanded } ?: 1
    val sospechosos by remember { sospechososViewModel.sospechosos }

    LaunchedEffect(CLAVE_CARGA_INICIAL) {
        sospechososViewModel.cargarSospechosos()
    }

    ScreenSeleccionJugadores(sospechosos, columnas, {})
}

@Composable
private fun ScreenSeleccionJugadores(
    sospechosos: List<Sospechoso>,
    columnas: Int,
    onSospechososClicado: (Sospechoso) -> Unit
) {
    ScreenVerde(
        configuracionToolbar = NavegadorCreacion.ConfiguracionToolbar(
            titulo = NavegadorCreacion.ConfiguracionToolbar.titulo("Libro de secretos")
        )
    ) {
        Column(Modifier.padding(MargenEstandar)) {
            LibroSecretos(sospechosos, columnas, onSospechososClicado)
        }
    }
}

@NightAndDay
@Composable
private fun PreviewScreen() {
    ScreenSeleccionJugadores(sospechosos(5), 1, { })
}