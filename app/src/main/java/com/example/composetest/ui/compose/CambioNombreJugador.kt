package com.example.composetest.ui.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.modifiers.onLongClick
import com.example.composetest.ui.compose.sampledata.jugadores
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.theme.TextoPresionado
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.compose.widget.TituloDefaults
import com.example.composetest.ui.compose.widget.dialog.AdelaidaTextFieldDialog
import com.example.composetest.ui.viewmodel.CambioNombreJugadorViewModel
import com.example.composetest.ui.viewmodel.CambioNombreJugadorViewModel.EstadoCambioNombre

@Composable
fun NombreJugadorEditable(
    jugador: Jugador,
    idPartida: Long?,
    onNombrePulsado: ((Jugador) -> Unit)?,
    onComprobarNombreRepetido: ((String) -> Boolean)?,
    modifier: Modifier = Modifier,
    nivelTitulo: NivelTitulo = NivelTitulo.Nivel2,
    textAlign: TextAlign = nivelTitulo.textAlign
) {
    val viewModel: CambioNombreJugadorViewModel = hiltViewModel()
    viewModel.inicializar(idPartida, onComprobarNombreRepetido)

    NombreJugador(jugador,
        {
            onNombrePulsado?.invoke(jugador)
            viewModel.mostrarDialogo(jugador)
        },
        viewModel.estadoDialogo,
        viewModel::onCambioNombreEnDialogo,
        viewModel::cerrarDialogo,
        viewModel::cambiarNombre,
        modifier,
        nivelTitulo,
        textAlign
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NombreJugador(
    jugador: Jugador,
    onNombrePulsado: (Jugador) -> Unit,
    estadoDialogo: State<EstadoCambioNombre?>,
    onCambioNombreEnDialogo: (EstadoCambioNombre, String) -> Unit,
    cerrarDialogo: () -> Unit,
    onCambiarNombre: (EstadoCambioNombre) -> Unit,
    modifier: Modifier = Modifier,
    nivelTitulo: NivelTitulo = NivelTitulo.Nivel2,
    textAlign: TextAlign = nivelTitulo.textAlign
) {
    val colorPorDefecto = TituloDefaults.textColor
    var colorTexto by remember { mutableStateOf(colorPorDefecto) }

    fun onTituloPresionado(estaPresionado: Boolean) {
        colorTexto = TextoPresionado.takeIf { estaPresionado } ?: colorPorDefecto
    }
    Titulo(jugador.nombre,
        Modifier
            .onLongClick(onBeingLongPressed = ::onTituloPresionado) { onNombrePulsado(jugador) }
            .then(modifier),
        nivelTitulo, textAlign, colorTexto)

    DialogoCambioNombreJugador(jugador, estadoDialogo, onCambioNombreEnDialogo, onCambiarNombre,
        cerrarDialogo)
}

@Composable
fun DialogoCambioNombreJugador(
    jugador: Jugador,
    estado: State<EstadoCambioNombre?>,
    onCambioNombreEnDialogo: (EstadoCambioNombre, String) -> Unit,
    onCambiarNombre: (EstadoCambioNombre) -> Unit,
    cerrarDialogo: () -> Unit,
) {
    val estado by remember { estado }

    estado
        ?.takeIf { it.jugador.esElMismoQue(jugador) }
        ?.let { estado ->
            AdelaidaTextFieldDialog(
                "¿Cómo vamos a llamar a ${estado.jugador.nombre} ahora?",
                "Nombre",
                estado.nombre,
                { nombreNuevo -> onCambioNombreEnDialogo(estado, nombreNuevo) },
                "Cambiar", { onCambiarNombre(estado) },
                "Cancelar", { cerrarDialogo() },
                estado.error
            )
        }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun PreviewCambioNombre() {
    ScreenPreviewMarron {
        val jugador = jugadores("Pedrito")[0]
        val estado = EstadoCambioNombre(jugador, "Rafael")

        NombreJugador(
            jugador,
            {},
            mutableStateOf(estado),
            { _, _ -> },
            {},
            {},
        )
    }
}