package com.example.composetest.ui.compose.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.R
import com.example.composetest.extensions.get
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.Equis
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.navegacion.NavegadorCreacion
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.AdelaidaButton
import com.example.composetest.ui.compose.widget.AdelaidaButtonText
import com.example.composetest.ui.compose.widget.AdelaidaTextField
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.viewmodel.SeleccionJugadoresViewModel
import com.example.composetest.ui.viewmodel.SeleccionJugadoresViewModel.EstadoNombreJugador

@Composable
fun ScreenSeleccionJugadores(
    onNavegarAlSiguientePaso: (partida: Partida) -> Unit,
    onMensaje: (Mensaje) -> Unit
) {
    val viewModel: SeleccionJugadoresViewModel = hiltViewModel()
    viewModel.onMensaje = onMensaje


    ScreenSeleccionJugadores(
        viewModel.nombrePartida,
        viewModel::alCambiarNombrePartida,
        viewModel.listadoJugadores,
        viewModel.nuevoJugador,
        viewModel::actualizarNuevoJugador,
        viewModel.errorJugadorRepetido,
        viewModel::guardarJugador,
        viewModel::eliminarJugador,
        viewModel.puedeIniciarPartida,
        { viewModel.mostrarDialogoComenzar() },
        viewModel.mostrarDialogoComenzar,
        viewModel::ocultarDialogoComenzar,
        { viewModel.guardarJugadores(onNavegarAlSiguientePaso) },
    )
}

@Composable
private fun ScreenSeleccionJugadores(
    nombrePartida: State<String>,
    onCambiarNombrePartida: (String) -> Unit,
    listadoJugadores: State<List<String>>,
    nuevoJugador: State<EstadoNombreJugador>,
    onNombreJugadorCambiado: (String) -> Unit,
    errorJugadorRepetido: State<String?>,
    guardarJugador: (String) -> Unit,
    eliminarJugador: (String) -> Unit,
    puedeIniciarPartida: State<Boolean>,
    mostrarDialogoComenzar: () -> Unit,
    dialogoComenzarAbierto: State<Boolean>,
    ocultarDialogoComenzar: () -> Unit,
    guardarJugadores: () -> Unit,
) {

    Screen(
        configuracionToolbar = NavegadorCreacion.ConfiguracionToolbar(
            titulo = NavegadorCreacion.ConfiguracionToolbar.nombrePartida(
                nombrePartida, onCambiarNombrePartida, true)
        )
    ) {
        Box(Modifier.padding(MargenEstandar)) {
            Column {
                FormularioNombreJugador(
                    nuevoJugador, onNombreJugadorCambiado,
                    errorJugadorRepetido, guardarJugador
                )
                ListadoJugadores(listadoJugadores, eliminarJugador, Modifier.weight(1f))
                IniciarPartida(puedeIniciarPartida, mostrarDialogoComenzar)
            }

            DialogoConfirmacionComenzar(
                dialogoComenzarAbierto,
                guardarJugadores,
                ocultarDialogoComenzar
            )
        }
    }
}

//@Composable
//private fun NombreDeLaPartida(
//    nombrePartida: State<String?>,
//    onCambiarNombrePartida: (String) -> Unit
//) {
//    val nombrePartida by remember { nombrePartida }
//    NombrePartida(nombrePartida, onCambiarNombrePartida, creando = true)
//}

@Composable
private fun FormularioNombreJugador(
    nuevoJugador: State<EstadoNombreJugador>,
    onNombreJugadorCambiado: (String) -> Unit,
    errorJugadorRepetido: State<String?>,
    guardarJugador: (String) -> Unit,
) {
    val nombreJugador by remember { nuevoJugador }
    val error by remember { errorJugadorRepetido }

    NombreJugador(nombreJugador.nombre, onNombreJugadorCambiado, error, guardarJugador)
    GuardarJugador(nombreJugador.nombre, nombreJugador.nombre.isNotBlank() && error == null, guardarJugador)
}

@Composable
private fun NombreJugador(
    nombreJugador: String,
    onNombreJugadorCambiado: (String) -> Unit,
    error: String?,
    guardarJugador: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    AdelaidaTextField(
        nombreJugador,
        onNombreJugadorCambiado,
        "Nombre del jugador",
        error
    ) {
        if (it.isBlank()) {
            focusManager.clearFocus(true)
        } else {
            guardarJugador(nombreJugador)
        }
    }
}

@Composable
private fun GuardarJugador(
    nombre: String,
    enabled: Boolean,
    guardarJugador: (String) -> Unit
) {
    AdelaidaButton(
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        onClick = { guardarJugador(nombre) },
    ) {
        AdelaidaButtonText("AÑADIR JUGADOR")
    }
}

@Composable
private fun ListadoJugadores(
    listadoJugadores: State<List<String>>,
    eliminarJugador: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listadoJugadores by remember { listadoJugadores }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp)
        .background(Tema.colors.fondoListas)
        .then(modifier)
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(listadoJugadores, { it }) {
                JugadorEnLaLista(it, eliminarJugador)
            }
        }
    }
}

@Composable
private fun LazyItemScope.JugadorEnLaLista(
    string: String,
    eliminarJugador: (String) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .animateItem(), verticalAlignment = Alignment.CenterVertically
    ) {
        Titulo(
            string,
            Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            NivelTitulo.Nivel2,
            TextAlign.Center
        )
        Equis({ eliminarJugador(string) }, "Eliminar jugador")
    }
}

@Composable
fun IniciarPartida(puedeIniciarPartida: State<Boolean>, mostrarDialogoComenzar: () -> Unit) {
    val puedeIniciarPartida by remember { puedeIniciarPartida }

    AdelaidaButton(
        onClick = { mostrarDialogoComenzar() },
        modifier = Modifier.fillMaxWidth(),
        enabled = puedeIniciarPartida,
    ) {
        AdelaidaButtonText(R.string.boton_iniciar_partida.get().uppercase())
    }
}

@Composable
private fun DialogoConfirmacionComenzar(
    mostrarDialogoComenzar: State<Boolean>,
    guardarJugadores: () -> Unit,
    ocultarDialogoComenzar: () -> Unit,
) {
    val mostrarDialogoComenzar by remember { mostrarDialogoComenzar }

    if (mostrarDialogoComenzar) {
        AdelaidaButtonDialog("¿Estos son todos los jugadores?",
            arrayOf(
                OpcionDialogo("Sí, empecemos a jugar", null) { guardarJugadores() },
                OpcionDialogo("Un momento", null) { ocultarDialogoComenzar() },
            ),
            onDismiss = { ocultarDialogoComenzar() }
        )
    }
}

@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@SuppressLint("UnrememberedMutableState")
@Composable
private fun NuevaPartida() {
    ScreenSeleccionJugadores(
        mutableStateOf("Este es el nombre de una partida cualquiera"),
        {},
        mutableStateOf(listOf("Pancracio", "Antonella", "Maldita sea, hostia puta, qué pedazo de nombre tan largo tiene este puto mierda")),
        mutableStateOf(EstadoNombreJugador("Maribel")),
        {},
        mutableStateOf("No se puede uno llamar Maribel"),
        {},
        {},
        puedeIniciarPartida = mutableStateOf(true),
        {},
        dialogoComenzarAbierto = mutableStateOf(false),
        {},
        {},
    )
}
