import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.AccionProhibida
import com.example.composetest.ui.compose.MatrizJugadores
import com.example.composetest.ui.compose.dialog.ConjuntoOpciones
import com.example.composetest.ui.compose.dialog.DialogoCompra
import com.example.composetest.ui.compose.dialog.DialogoElementoClicado
import com.example.composetest.ui.compose.dialog.DialogoPistaPendiente
import com.example.composetest.ui.compose.dialog.DialogoRobo
import com.example.composetest.ui.compose.sampledata.jugadores
import com.example.composetest.ui.compose.sampledata.pistas
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.manager.AsuntoTurbio
import com.example.composetest.ui.manager.GestorPistaPendiente
import com.example.composetest.ui.viewmodel.TabJugadoresViewModel

@Composable
fun TabJugadores(
    asuntoTurbio: AsuntoTurbio,
    idPartida: Long?,
    jugadores: List<Jugador>?,
    sePuedeComprar: Boolean,
    iniciarAsuntoTurbio: (AsuntoTurbio) -> Unit,
    onAccionProhibida: (AccionProhibida) -> Unit
) {
    val tabViewModel: TabJugadoresViewModel = hiltViewModel()
    // TODO Melero: 12/2/25 Esto se llama de nuevo al girar y entonces se reinician las opcionesClicado
    //  Pero parece que hace falta hacerlo, o no funciona el botón de pistaPendiente
    tabViewModel.inicializar(idPartida, jugadores, asuntoTurbio, iniciarAsuntoTurbio, onAccionProhibida)

    ListaJugadores(idPartida, jugadores, sePuedeComprar)
    DialogoOpcionesCasilla(jugadores, idPartida, onAccionProhibida)
    DialogoDinero(tabViewModel)
    DialogoBrandy(tabViewModel)
    DialogoDevolverAlTablero(tabViewModel)
}

@Composable
private fun ListaJugadores(idPartida: Long?, jugadores: List<Jugador>?, sePuedeComprar: Boolean) {
    val tabViewModel: TabJugadoresViewModel = hiltViewModel()
    val queMostrar by remember { tabViewModel.queMostrar }

    MatrizJugadores(
        idPartida,
        jugadores,
        queMostrar,
        tabViewModel.opcionesClicado,
        TabJugadoresViewModel.ConfigMarcadorDinero(true, sePuedeComprar, tabViewModel::iniciarCompra),
        tabViewModel::sePuedeClicar,
        tabViewModel::onCasillaClicada,
        { tabViewModel.onCambiarNombreClicado() },
        tabViewModel::comprobarNombreRepetido,
        tabViewModel.jugadorConCartasAbiertas,
        tabViewModel::onMostrarCartas,
        tabViewModel::onPistaPendienteClicada,
        tabViewModel::onCancelarAsignacionPistaPendiente,
        tabViewModel::onDevolverPistaPendienteAlTablero,
    )
}

@Composable
private fun DialogoOpcionesCasilla(
    jugadores: List<Jugador>?,
    idPartida: Long?,
    onAccionProhibida: (AccionProhibida) -> Unit
) {
    val tabViewModel: TabJugadoresViewModel = hiltViewModel()
    val elementoClicado by remember { tabViewModel.elementoClicado }

    noneNull(elementoClicado, jugadores, idPartida) { elemento, jugadores, id ->
        DialogoElementoClicado(
            elemento.elemento, jugadores, elemento.poseedor, ConjuntoOpciones.JUGADOR, id,
            onAccionProhibida, tabViewModel::onResultadoDeClicado
        )
    }
}

@Composable
fun DialogoDinero(viewModel: TabJugadoresViewModel) {
    val accionCompra by remember { viewModel.mostrarDialogoCompra }

    accionCompra?.let {
        DialogoCompra(
            accionCompra = it,
            comprarPista = viewModel::comprarPista,
            comprarSecreto = viewModel::comprarSecreto,
            cerrarDialogo = viewModel::cerrarDialogoDinero
        )
    }
}

@Composable
fun DialogoBrandy(viewModel: TabJugadoresViewModel) {
    val accionRobo by remember { viewModel.mostrarDialogoRobo}

    accionRobo?.let {
        DialogoRobo(
            accionRobo = it,
            robarDeLaVitrina = viewModel::robarDeLaVitrina,
            robarDelaMano = viewModel::robarDeLaMano,
            cerrarDialogo = viewModel::cerrarDialogoBrandy
        )
    }
}

@Composable
fun DialogoDevolverAlTablero(viewModel: TabJugadoresViewModel) {
    val accionPistaPendiente by remember { viewModel.mostrarDialogoAccionPistaPendiente }

    accionPistaPendiente?.let {
        DialogoPistaPendiente(
            it.pistaPendiente,
            it.accion,
            viewModel::ejecutarPistaPendiente,
            viewModel::cerrarDialogoDevolucion
        )
    }
}

@Composable
@Preview(name = "ListaJugadores")
private fun P1() {
    ScreenPreviewMarron {
        ListaJugadores(7L, jugadores(), false)
    }
}

@Composable
@Preview(name = "DialogoPistaPendiente")
private fun P2() {
    ScreenPreviewMarron {
        val pistaPendinte = pistas()[0]
        val estado = TabJugadoresViewModel.EstadoPistaPendiente(
            pistaPendinte, GestorPistaPendiente.Accion.DevolucionATablero({}))

        DialogoPistaPendiente(estado.pistaPendiente, estado.accion, {}, {})
    }
}