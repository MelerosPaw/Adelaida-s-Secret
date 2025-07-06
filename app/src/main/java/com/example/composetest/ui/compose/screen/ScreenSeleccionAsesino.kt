package com.example.composetest.ui.compose.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.List
import androidx.compose.material.icons.automirrored.sharp.MenuBook
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.extensions.joinToStringHumanReadable
import com.example.composetest.model.Partida
import com.example.composetest.model.Sospechoso
import com.example.composetest.ui.compose.Cargando
import com.example.composetest.ui.compose.CheckboxIcon
import com.example.composetest.ui.compose.CustomCheckbox
import com.example.composetest.ui.compose.CustomIcon
import com.example.composetest.ui.compose.HtmlSpan
import com.example.composetest.ui.compose.LibroSecretos
import com.example.composetest.ui.compose.navegacion.CLAVE_CARGA_INICIAL
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.navegacion.NavegadorCreacion.ConfiguracionToolbar
import com.example.composetest.ui.compose.sampledata.sospechosos
import com.example.composetest.ui.compose.theme.ListItemSubtitleIndentation
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.MargenSuperiorTituloToolbar
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.AdelaidaDivider
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.viewmodel.SeleccionAsesinoViewModel
import com.example.composetest.ui.viewmodel.SospechososViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ScreenSeleccionAsesino(
    onConfiguracionToolbarCambiada: (ConfiguracionToolbar) -> Unit,
    onNavegarAlSiguientePaso: (partida: Partida) -> Unit,
    onMensaje: (Mensaje) -> Unit
) {
    val pantallaViewModel: SeleccionAsesinoViewModel = hiltViewModel()
    val sospechososViewModel: SospechososViewModel = hiltViewModel()
    pantallaViewModel.onMensaje = onMensaje

    val sospechosos by remember { sospechososViewModel.sospechosos }

    val isLandscape = isLandscape()

    LaunchedEffect(CLAVE_CARGA_INICIAL) {
        val columnas = 2.takeIf { isLandscape } ?: 1
        pantallaViewModel.onCambiarColumnas(columnas)

        if (sospechososViewModel.sospechosos.value.isEmpty()) {
            sospechososViewModel.cargarSospechosos()
        }
    }

    ScreenSeleccionAsesino(
        onConfiguracionToolbarCambiada,
        sospechosos,
        pantallaViewModel::seleccionarSospechoso,
        pantallaViewModel.mostrarConfirmacionSospechoso,
        { pantallaViewModel.confirmarSospechoso(it, onNavegarAlSiguientePaso) },
        pantallaViewModel::ocultarDialogoConfirmacion,
        pantallaViewModel.visualizacion,
        pantallaViewModel::onCambiarVisualizacion
    )
}

@Composable
private fun ScreenSeleccionAsesino(
    onConfiguracionToolbarCambiada: (ConfiguracionToolbar) -> Unit,
    sospechosos: List<Sospechoso>,
    onSospechosoSeleccionado: (Sospechoso) -> Unit,
    mostrarConfirmacionSospechoso: State<Sospechoso?>,
    confirmarSospechoso: (Sospechoso) -> Unit,
    ocultarDialogoConfirmacion: () -> Unit,
    visualizacion: State<VisualizacionSospechoso>,
    onCambiarVisualizacion: (VisualizacionSospechoso) -> Unit
) {
    val visualizacion by remember { visualizacion }

    Screen(
        configuracionToolbar = ConfiguracionToolbar(
        titulo = ConfiguracionToolbar.titulo("Sospechosos"),
        actions = { CambiarVisualizacion(visualizacion, onCambiarVisualizacion) }
    )
    ) {

        Column(
            Modifier
                .padding(horizontal = MargenEstandar)
                .padding(top = MargenSuperiorTituloToolbar, bottom = MargenEstandar)
        ) {
            Box {
                Column {
                    Explicacion()
                    Sospechosos(sospechosos, visualizacion, onSospechosoSeleccionado)
                }

                CargandoSospechosos(sospechosos.isEmpty())
                DialogoConfirmarSospechoso(
                    mostrarConfirmacionSospechoso,
                    confirmarSospechoso,
                    ocultarDialogoConfirmacion
                )
            }
        }
    }
}

@Composable
fun CambiarVisualizacion(
    visualizacion: VisualizacionSospechoso,
    onCambiarVisualizacion: (VisualizacionSospechoso) -> Unit
) {
    CustomCheckbox(
        visualizacion is VisualizacionSospechoso.LibroSecretos,
        CustomIcon(
            CheckboxIcon(Icons.AutoMirrored.Sharp.MenuBook, "Cambiar a descripción del sospechoso"),
            CheckboxIcon(Icons.AutoMirrored.Sharp.List, "Cambiar a listado")
        )
    ) { marcado ->
        val cambio = VisualizacionSospechoso.LibroSecretos(visualizacion.columnas).takeIf { marcado }
            ?: VisualizacionSospechoso.Lista(visualizacion.columnas)
        onCambiarVisualizacion(cambio)
    }
}

@Composable
private fun Explicacion() {
    val html = HtmlSpan(
        "Selecciona el sospechoso que será el culpable:"
    )
    AdelaidaText(html.text, spans = html.spans, modifier = Modifier.padding(bottom = MargenEstandar))
}

@Composable
private fun Sospechosos(
    sospechosos: List<Sospechoso>,
    visualizacion: VisualizacionSospechoso,
    onSospechosoSeleccionado: (Sospechoso) -> Unit
) {
    when (visualizacion) {
        is VisualizacionSospechoso.Lista -> ListaSospechosos(sospechosos, visualizacion.columnas, onSospechosoSeleccionado)
        is VisualizacionSospechoso.LibroSecretos -> LibroSecretos(sospechosos, visualizacion.columnas, onSospechosoSeleccionado)
    }
}

@Composable
private fun ListaSospechosos(
    sospechosos: List<Sospechoso>,
    columnas: Int,
    onSospechosoSeleccionado: (Sospechoso) -> Unit
) {
    ListadoSospechosos(sospechosos, columnas, onSospechosoSeleccionado)
}

@Composable
private fun ListadoSospechosos(
    sospechosos: List<Sospechoso>,
    columnas: Int,
    onSospechosoSeleccionado: (Sospechoso) -> Unit,
) {
    if (sospechosos.isNotEmpty()) {
        LazyVerticalGrid(columns = GridCells.Fixed(columnas), modifier = Modifier.background(Tema.colors.fondoListas),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(sospechosos, { _, it -> it.nombre }) { index, it ->
                Column {
                    SospechosoItem(onSospechosoSeleccionado, it)

                    if (index < sospechosos.size - 1) {
                        AdelaidaDivider(Modifier.padding(horizontal = ListItemSubtitleIndentation))
                    }
                }
            }
        }
    }
}

@Composable
private fun LazyGridItemScope.SospechosoItem(
    onSospechosoSeleccionado: (Sospechoso) -> Unit,
    sospechoso: Sospechoso
) {
    ListItem(
        modifier = Modifier
            .animateItem()
            .clickable { onSospechosoSeleccionado(sospechoso) },
        headlineContent = { Titulo(sospechoso.nombre, nivel = NivelTitulo.Nivel2) },
        supportingContent = {
            AdelaidaText(
                "Rasgos: ${sospechoso.rasgos.joinToStringHumanReadable { it.idPista }}\n" +
                        "Secreto: ${sospechoso.secreto.idPista}, asociado con el ${sospechoso.secreto.idSecretoVinculado}",
                modifier = Modifier.padding(start = ListItemSubtitleIndentation)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun DialogoConfirmarSospechoso(
    mostrarConfirmacionSospechoso: State<Sospechoso?>,
    confirmarSospechoso: (Sospechoso) -> Unit,
    ocultarDialogo: () -> Unit
) {
    val sospechosoSeleccionado by remember { mostrarConfirmacionSospechoso }

    sospechosoSeleccionado?.let { sospechoso ->
        val elAsesino = when (sospechoso.genero) {
            Sospechoso.Genero.HOMBRE -> "el asesino"
            Sospechoso.Genero.MUJER -> "la asesina"
        }
        val articulo = when (sospechoso.genero) {
            Sospechoso.Genero.HOMBRE -> "el"
            Sospechoso.Genero.MUJER -> "la"
        }

        val este = when (sospechoso.genero) {
            Sospechoso.Genero.HOMBRE -> "este"
            Sospechoso.Genero.MUJER -> "esta"
        }

        AdelaidaButtonDialog("¿Quieres que ${sospechoso.nombre} sea $elAsesino?",
            arrayOf(

                OpcionDialogo("¡Sí, $este es $articulo culpable!", null) {
                    confirmarSospechoso(sospechoso)
                },
                OpcionDialogo("No, mejor otro sospechoso", null) { ocultarDialogo() }
            ),
            onDismiss = { ocultarDialogo() }
        )
    }
}

@Composable
private fun CargandoSospechosos(mostrar: Boolean) {
    if (mostrar) {
        Box(Modifier.fillMaxSize(), propagateMinConstraints = true) {
            Cargando()
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(name = "Lista de sospechosos - Claro")
@Preview(name = "Lista de sospechosos - Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun P1() {
    val sospechosos = sospechosos(2)
    val mostrarConfirmacion = mutableStateOf(null)
    ScreenSeleccionAsesino(
        {},
        sospechosos = sospechosos,
        {},
        mostrarConfirmacionSospechoso = mostrarConfirmacion,
        {},
        {},
        visualizacion = mutableStateOf(VisualizacionSospechoso.LibroSecretos(2)),
        {})
}

sealed class VisualizacionSospechoso(val columnas: Int) {
    class Lista(columnas: Int): VisualizacionSospechoso(columnas)
    class LibroSecretos(columnas: Int): VisualizacionSospechoso(columnas)
}