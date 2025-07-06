package com.example.composetest.ui.compose.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.screen.ScreenMediodiaTarde
import com.example.composetest.ui.compose.screen.ScreenNoche
import kotlinx.serialization.Serializable

class NavegadorRondas(val navController: NavController) {

    companion object {

        @Composable
        fun grafo(
            navController: NavHostController,
            partida: Partida,
            onMensaje: (Mensaje) -> Unit,
            filtrosAbiertos: State<Boolean>,
            onCerrarFiltros: () -> Unit,
            onMostrarPapelera: (Boolean) -> Unit,
            cambioRondaSolicitado: Boolean,
            onCondicionesCambioRondaSatisfechas: (Boolean) -> Unit,
            onMostrarMensajeAbandonar : () -> Unit,
        ): NavGraphBuilder.() -> Unit {

            return {
                composable<Destinos.MediodiaTarde> {
                    val cambiarATarde = navController.currentBackStackEntry?.savedStateHandle
                        ?.getLiveData<EventoBooleano>(Destinos.MediodiaTarde.CLAVE_PASAR_A_TARDE)
                        ?.observeAsState(EventoBooleano(false))

                    ScreenMediodiaTarde(
                        partida,
                        onMensaje,
                        filtrosAbiertos,
                        onCerrarFiltros,
                        onMostrarPapelera,
                        cambioRondaSolicitado,
                        onCondicionesCambioRondaSatisfechas,
                        cambiarATarde,
                        onMostrarMensajeAbandonar,
                    )
                }
                composable<Destinos.Noche> {
                    ScreenNoche(
                        partida,
                        cambioRondaSolicitado,
                        onCondicionesCambioRondaSatisfechas,
                        onMensaje
                    )
                }
            }
        }

        val PRIMERA_RONDA = Partida.Ronda.MEDIODIA

        fun obtenerSiguienteRonda(ronda: Partida.Ronda): Partida.Ronda = when (ronda) {
            Partida.Ronda.MANANA -> Partida.Ronda.MEDIODIA
            Partida.Ronda.MEDIODIA -> Partida.Ronda.TARDE
            Partida.Ronda.TARDE -> Partida.Ronda.NOCHE
            Partida.Ronda.NOCHE -> Partida.Ronda.MANANA
            Partida.Ronda.NO_VALIDO -> Partida.Ronda.NO_VALIDO
        }

        fun obtenerDestinoDeRondaActual(ronda: Partida.Ronda): PantallaRonda? = when(ronda) {
            Partida.Ronda.MANANA -> Destinos.Manana
            Partida.Ronda.MEDIODIA, Partida.Ronda.TARDE -> Destinos.MediodiaTarde
            Partida.Ronda.NOCHE -> Destinos.Noche
            Partida.Ronda.NO_VALIDO -> null
        }
    }

    fun navegarARondaActual(partida: Partida) {
        when (partida.ronda) {
            Partida.Ronda.MANANA -> navegarAManana(partida)
            Partida.Ronda.MEDIODIA -> navegarAMediodia(partida)
            Partida.Ronda.TARDE -> navegarATarde(partida)
            Partida.Ronda.NOCHE -> navegarANoche(partida)
            Partida.Ronda.NO_VALIDO -> { /* No se puede navegar a ningún lado si la partida no es válida. */
            }
        }
    }

    private fun navegarAManana(partida: Partida) {
//        navController.navigate(SeleccionAsesino(partida.id))
    }

    private fun navegarAMediodia(partida: Partida) {
        // TODO Melero: 12/1/25 Navegar en lugar de tenerlo puesto a la fuerza
        val opciones = getOpcionesMenuPrincipalComoPadre()
        navController.navigate(Destinos.MediodiaTarde, opciones)
    }

    private fun navegarATarde(partida: Partida) {
        if (navController.currentDestination?.route == Destinos.MediodiaTarde::class.qualifiedName) {
            navController.currentBackStackEntry?.savedStateHandle
                ?.set(Destinos.MediodiaTarde.CLAVE_PASAR_A_TARDE, EventoBooleano(true))

        } else {
            val opciones = getOpcionesMenuPrincipalComoPadre()
            navController.navigate(Destinos.MediodiaTarde, opciones)
        }
    }

    private fun navegarANoche(partida: Partida) {
        val opciones = getOpcionesMenuPrincipalComoPadre()
        navController.navigate(Destinos.Noche, opciones)
    }

    private fun getOpcionesMenuPrincipalComoPadre(): NavOptions =
        NavOptions.Builder()
            .setPopUpTo<MenuPrincipal>(false)
            .build()

    class Destinos {

        @Serializable
        object Manana: PantallaRonda

        @Serializable
        object MediodiaTarde: PantallaRonda {
            const val CLAVE_PASAR_A_TARDE: String = "CLAVE_PASAR_A_TARDE"
        }

        @Serializable
        object Noche: PantallaRonda
    }

    interface PantallaRonda
}