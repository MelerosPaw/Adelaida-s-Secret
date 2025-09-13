package com.example.composetest.ui.compose.navegacion

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.composetest.ui.compose.NombrePartida
import com.example.composetest.ui.compose.dialog.DialogoCambioNombrePartida
import com.example.composetest.ui.compose.screen.ScreenCargarPartida
import com.example.composetest.ui.compose.screen.ScreenMain
import com.example.composetest.ui.compose.screen.ScreenNuevoTablero
import com.example.composetest.ui.compose.screen.ScreenPartida
import com.example.composetest.ui.compose.screen.ScreenSeleccionAsesino
import com.example.composetest.ui.compose.screen.ScreenSeleccionJugadores
import com.example.composetest.ui.compose.theme.AdelaidaTheme
import com.example.composetest.ui.compose.theme.AdelaidaThemeVerde
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.viewmodel.NavegacionViewModel
import com.example.composetest.ui.viewmodel.NombrePartidaViewModel

typealias PartidaModelo = com.example.composetest.model.Partida
typealias JugadorModelo = com.example.composetest.model.Jugador
typealias PartidaModeloCreacion = com.example.composetest.model.Partida.EstadoCreacion

const val CLAVE_CARGA_INICIAL = "KEY_INITIAL_DATA"

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Navegacion() {
    val viewModel = hiltViewModel<NavegacionViewModel>()
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = MenuPrincipal,
        builder = grafoNavegacion(navController, viewModel::onConfiguracionToolbarCambiada)
    )
}
// endregion

@Composable
fun SetUpStatusBar(colorStatusBar: Color) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        val context = LocalContext.current
        val isLightTheme = !isSystemInDarkTheme()
        val statusColor = colorStatusBar.toArgb()

        SideEffect {
            (context as? Activity)?.window?.let { window ->
                window.statusBarColor = statusColor
                WindowCompat
                    .getInsetsController(window, view)
                    .isAppearanceLightStatusBars = false // El color de la status bar siempre es oscuro
            }
        }
    }
}

@Composable
private fun grafoNavegacion(
    navController: NavHostController,
    cambiarConfiguracionToolbar: (NavegadorCreacion.ConfiguracionToolbar) -> Unit
): NavGraphBuilder.() -> Unit {

    val navegadorCreacion = NavegadorCreacion(navController)

    fun mostrarMensaje(mensaje: Mensaje) {
        navController.navigate(mensaje)
    }

    fun atras() {
        navController.popBackStack()
    }

    return {
        composable<MenuPrincipal> {
            ScreenMain(
                cambiarConfiguracionToolbar,
                onNavegarATableroDebug = { navController.navigate(NuevoTablero(null)) },
                onNavegarANuevaPartida = { navController.navigate(SeleccionNombre) },
                onNavegarACargarPartida = { navController.navigate(CargarPartida) }
            )
        }

        dialog<SeleccionNombre> {
            AdelaidaThemeVerde {
                val vm = hiltViewModel<NombrePartidaViewModel>()
                vm.mostrarDialogoCambioNombre()

                DialogoCambioNombrePartida(
                    "¿Cómo se llamará esta partida?", null,
                    { vm.crearPartida(it, navegadorCreacion::navegarAlPasoActual) },
                    { navController.popBackStack() },
                    false
                )
            }
        }

        composable<SeleccionAsesino> {
            ScreenSeleccionAsesino(cambiarConfiguracionToolbar,
                navegadorCreacion::navegarAlPasoActual, ::mostrarMensaje)
        }

        composable<NuevoTablero> {
            ScreenNuevoTablero(navegadorCreacion::navegarAlPasoActual, ::mostrarMensaje)
        }

        composable<SeleccionJugadores> {
            ScreenSeleccionJugadores(navegadorCreacion::navegarAlPasoActual, ::mostrarMensaje)
        }

        composable<Partida> {
            ScreenPartida(cambiarConfiguracionToolbar, ::atras, ::mostrarMensaje)
        }

        composable<CargarPartida> {
            ScreenCargarPartida(cambiarConfiguracionToolbar,
                navegadorCreacion::navegarAlPasoActual, ::atras, ::mostrarMensaje)
        }

        dialog<Mensaje> {
            AdelaidaTheme {
                val mensaje: String = it.toRoute<Mensaje>().mensaje

                AdelaidaButtonDialog(
                    mensaje,
                    arrayOf(OpcionDialogo("Cerrar", null, true) { navController.popBackStack() }),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

class NavegadorCreacion(val navController: NavController) {

    companion object {

        val PRIMER_ESTADO = PartidaModeloCreacion.SELECCION_ASESINO

        fun obtenerSiguienteEstado(estado: PartidaModeloCreacion): PartidaModeloCreacion =
            when (estado) {
                PartidaModeloCreacion.SELECCION_ASESINO -> PartidaModeloCreacion.SELECCION_TABLERO
                PartidaModeloCreacion.SELECCION_TABLERO -> PartidaModeloCreacion.SELECCION_JUGADORES
                PartidaModeloCreacion.SELECCION_JUGADORES -> PartidaModeloCreacion.PARTIDA_EMPEZADA
                PartidaModeloCreacion.PARTIDA_EMPEZADA -> PartidaModeloCreacion.PARTIDA_EMPEZADA // Tiene que pasar a finalizado, pero aún no existe
                PartidaModeloCreacion.NO_VALIDO -> PartidaModeloCreacion.NO_VALIDO
            }
    }

    fun navegarAlPasoActual(partida: PartidaModelo) {
        when (partida.estadoCreacion) {
            PartidaModeloCreacion.SELECCION_ASESINO -> navegarASeleccionAsesino(partida)
            PartidaModeloCreacion.SELECCION_TABLERO -> navegarANuevoTablero(partida)
            PartidaModeloCreacion.SELECCION_JUGADORES -> navegarASeleccionJugadores(partida)
            PartidaModeloCreacion.PARTIDA_EMPEZADA -> navegarAPartidaEmpezada(partida)
            PartidaModeloCreacion.NO_VALIDO -> {/* No se puede navegar a ningún lado si la partida no es válida. */ }
        }
    }

    private fun navegarASeleccionAsesino(partida: PartidaModelo) {
        navController.navigate(SeleccionAsesino(partida.id))
    }

    private fun navegarANuevoTablero(partida: PartidaModelo) {
        val options = NavOptions.Builder()
            .setPopUpTo<MenuPrincipal>(false)
            .build()
        val destination = NuevoTablero(partida.id)
        navController.navigate(destination, options)
    }

    private fun navegarASeleccionJugadores(partida: PartidaModelo) {
        val opciones = NavOptions.Builder()
            .setPopUpTo<MenuPrincipal>(false)
            .build()
        navController.navigate(SeleccionJugadores(partida.id, partida.nombre), opciones)
    }

    private fun navegarAPartidaEmpezada(partida: PartidaModelo) {
        val opciones = NavOptions.Builder()
            .setPopUpTo<MenuPrincipal>(false)
            .build()
        navController.navigate(Partida(partida.id, partida.nombre), opciones)
    }

    data class ConfiguracionToolbar(
        val titulo: @Composable () -> Unit = { titulo("Los Secretos de Adelaida") },
        val actions: @Composable RowScope.() -> Unit = { },
        val iconoNavegacion: @Composable () -> Unit = { },
        val colorToolbar: Color = Color.Unspecified,
        val colorStatusBar: Color = Color.Unspecified,
    ) {

        companion object {

            val colorToolbar: Color
                @Composable get() = Tema.colors.toolbarContainer

            val colorStatusBar: Color
                @Composable get() = Tema.colors.statusBar

            @Composable
            fun titulo(titulo: String): @Composable () -> Unit = {
                Titulo(titulo, nivel = NivelTitulo.TituloPantalla,
                    color = Tema.colors.toolbarContent, useMarquee = true)
            }

            @Composable
            fun nombrePartida(
                nombrePartida: State<String>,
                actualizarNombrePartida: (String) -> Unit,
                enabled: Boolean
            ): @Composable () -> Unit {
                val nombrePartida by remember { nombrePartida }
                return nombrePartida(nombrePartida, actualizarNombrePartida, enabled = enabled)
            }

            @Composable
            fun nombrePartida(
                nombrePartida: String?,
                actualizarNombrePartida: (String) -> Unit,
                enabled: Boolean,
                onClicked: (() -> Unit) = {},
            ): @Composable () -> Unit = {
                NombrePartida(nombrePartida, actualizarNombrePartida,
                    Modifier.padding(end = 8.dp).clickable { onClicked() },
                    enabled = enabled, tint = Tema.colors.toolbarContent)
            }
        }

        @Composable
        fun getToolbarContainerColor(): Color = colorToolbar.takeIf { it.isSpecified } ?: ConfiguracionToolbar.colorToolbar

        @Composable
        fun getStatusBarColor(): Color = colorStatusBar.takeIf { it.isSpecified } ?: ConfiguracionToolbar.colorStatusBar
    }
}