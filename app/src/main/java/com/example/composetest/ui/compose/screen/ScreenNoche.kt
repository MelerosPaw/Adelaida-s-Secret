package com.example.composetest.ui.compose.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.model.Baremo
import com.example.composetest.model.Jugador
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.DialogoRealizacionEvento
import com.example.composetest.ui.compose.NightAndDay
import com.example.composetest.ui.compose.NombreJugadorEditable
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.sampledata.eventos
import com.example.composetest.ui.compose.sampledata.eventosVo
import com.example.composetest.ui.compose.sampledata.jugadores
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.AdelaidaButton
import com.example.composetest.ui.compose.widget.AdelaidaDivider
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.compose.widget.EventoVO
import com.example.composetest.ui.compose.widget.ListadoBaremos
import com.example.composetest.ui.compose.widget.ListadoEventos
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.compose.widget.TituloEvento
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.AdelaidaDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.compose.widget.rememberListadoBaremosState
import com.example.composetest.ui.contracts.Consumidor
import com.example.composetest.ui.contracts.Intencion
import com.example.composetest.ui.contracts.Intencion.AbrirDialogoSeleccionManualEventos
import com.example.composetest.ui.contracts.Intencion.GuardarBaremo
import com.example.composetest.ui.contracts.Intencion.OcultarDialogoEjecucionEvento
import com.example.composetest.ui.contracts.Intencion.RealizarEventoSeleccionado
import com.example.composetest.ui.contracts.Intencion.SeleccionarEvento
import com.example.composetest.ui.contracts.Intencion.SeleccionarEventoAleatorio
import com.example.composetest.ui.viewmodel.Estados
import com.example.composetest.ui.viewmodel.NocheViewModel
import com.example.composetest.ui.viewmodel.NocheViewModel.EventoRealizandose
import com.example.composetest.ui.viewmodel.NocheViewModel.JugadorVO

@Composable
fun ScreenNoche(
    partida: Partida?,
    cambioRondaSolicitado: Boolean,
    onCondicionesCambioRondaSatisfechas: (Boolean) -> Unit,
    onMensaje: (Mensaje) -> Unit
) {
    val viewModel: NocheViewModel = hiltViewModel()
    viewModel.onMensaje = onMensaje
    viewModel.inicializar(partida, cambioRondaSolicitado, onCondicionesCambioRondaSatisfechas)

    ScreenNoche(
        viewModel.estados,
        viewModel.consumidor,
        partida?.id,
        viewModel::comprobarNombreRepetido,
    )
}

@Composable
private fun ScreenNoche(
    estados: Estados,
    consumidor: Consumidor,
    idPartida: Long?,
    comprobarNombreRepetido: (String) -> Boolean,
) {
    val scroll = rememberScrollState()
    Column(Modifier
        .fillMaxSize()
        .padding(horizontal = MargenEstandar)
        .verticalScroll(scroll)
    ) {
        SeccionBaremos(estados, consumidor, idPartida, comprobarNombreRepetido)
        AdelaidaDivider(Modifier.padding(top = 10.dp))
        SeccionEvento(estados, consumidor)
        AdelaidaDivider(Modifier.padding(top = 10.dp))
        SeccionVisitaAdelaida()
    }

    DialogoSeleccionBaremos(estados, consumidor)
    DialogoConfirmarEjecucionAhora(estados, consumidor)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun SeccionBaremos(
    estados: Estados,
    consumidor: Consumidor,
    idPartida: Long?,
    comprobarNombreRepetido: (String) -> Boolean
) {
    val jugadores by remember { estados.jugadores }

    Titulo("Baremos")
    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        jugadores.forEach {
            Column(
                Modifier
                    .clickable { consumidor.consumir(Intencion.AbrirDialogoBaremos(it)) }
                    .padding(top = MargenEstandar)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NombreJugadorEditable(
                    it, idPartida, { }, comprobarNombreRepetido,
                    nivelTitulo = NivelTitulo.Nivel3, textAlign = TextAlign.Center
                )
                Titulo(
                    it.idBaremo ?: "Sin baremo", nivel = NivelTitulo.Nivel1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DialogoSeleccionBaremos(estados: Estados, consumidor: Consumidor) {
    val estadoBaremos by remember { estados.estadoDialogoBaremos }
    val cerrarDialogoBaremos = { consumidor.consumir(Intencion.CerrarBaremo()) }

    estadoBaremos?.let {
        AdelaidaDialog(cerrarDialogoBaremos, DialogProperties(true, true, false), contentMustScroll = false) {
            val state = rememberListadoBaremosState(it.baremoSeleccionado, it.baremosNoSeleccionables)
            ListadoBaremos(state, it.jugador, it.baremos, { jugador, baremo -> consumidor.consumir(GuardarBaremo(jugador, baremo)) }, cerrarDialogoBaremos)
        }
    }
}

@Composable
private fun DialogoConfirmarEjecucionAhora(
    estados: Estados,
    consumidor: Consumidor,
) {
    val confirmarEjecutarEvento by remember { estados.confirmarEjecutarEvento }

    confirmarEjecutarEvento?.let { evento ->
        AdelaidaButtonDialog(
            "Este evento tiene lugar durante la noche. ¿Quieres ejecutarlo ahora?",
            arrayOf(
                OpcionDialogo("Sí", null) {
                    consumidor.consumir(OcultarDialogoEjecucionEvento(), RealizarEventoSeleccionado(evento))
                },
                OpcionDialogo("No", null) { consumidor.consumir(OcultarDialogoEjecucionEvento()) },
            ),
            onDismiss = { consumidor.consumir(OcultarDialogoEjecucionEvento()) }
        )
    }
}

@Composable
private fun SeccionEvento(
    estados: Estados,
    consumidor: Consumidor,
) {
    val eventoSeleccionado by remember { estados.eventoSeleccionado }
    val mostrarDialogo by remember { estados.mostrarDialogoSeleccionManualEvento }
    val yaSeHaRealizado by remember { estados.yaSeHaRealizado }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Titulo("Evento")
            if (yaSeHaRealizado) {
                AdelaidaText("Ya realizado", Modifier.padding(start = 10.dp))
            }
        }
        TituloEventoYBotones(estados, consumidor, eventoSeleccionado, yaSeHaRealizado)
        DialogoEventos(estados, consumidor, mostrarDialogo.mostrar, eventoSeleccionado, mostrarDialogo.ultimoEventoVisto)
        DialogoRealizacionEvento(estados, consumidor)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TituloEventoYBotones(
    estados: Estados,
    consumidor: Consumidor,
    eventoSeleccionado: EventoVO?,
    yaSeHaRealizado: Boolean,
) {
    val seleccionManual = { consumidor.consumir(AbrirDialogoSeleccionManualEventos()) }
    val seleccionEventoAleatorio = { consumidor.consumir(SeleccionarEventoAleatorio()) }
    val realizarEvento: () -> Unit =
        { eventoSeleccionado?.let { consumidor.consumir(RealizarEventoSeleccionado(it)) } }

    FlowRow(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        val esLandscape = isLandscape()
        val margenFin = (8.takeIf { esLandscape } ?: 0).dp
        val dimensiones = Modifier.fillMaxWidth().takeIf  { !esLandscape  } ?: Modifier.weight(1f)

        TituloEvento(
            eventoSeleccionado?.evento?.nombre,
            Tema.colors.texto,
            Modifier.then(dimensiones).padding(end = margenFin),
            TextAlign.Center.takeIf { !esLandscape } ?: TextAlign.Start
        )
        FlowRow(horizontalArrangement = Arrangement.End.takeIf { isLandscape() } ?: Arrangement.Center) {
            val medidaBoton = Modifier.fillMaxWidth()
                .takeUnless { esLandscape }
                ?: Modifier.wrapContentWidth()

            if (!yaSeHaRealizado) {
                AdelaidaButton(seleccionEventoAleatorio, "ALEATORIO", Modifier.padding(end = margenFin).then(medidaBoton))
                AdelaidaButton(seleccionManual, "SELECCIÓN MANUAL", medidaBoton)
                if (eventoSeleccionado?.sePuedeEjecutarAhora == true) {
                    AdelaidaButton(realizarEvento, "REALIZAR", Modifier.padding(end = margenFin).then(medidaBoton))
                }
            }
        }
    }
}

@Composable
private fun DialogoEventos(
    estados: Estados,
    consumidor: Consumidor,
    mostrarDialogo: Boolean,
    eventoSeleccionado: EventoVO?,
    ultimoVisto: EventoVO?,
) {
    val eventos by remember { estados.eventos }

    if (mostrarDialogo) {
        val dismiss: (EventoVO) -> Unit = {
            consumidor.consumir(Intencion.CerrarDialogoSeleccionManualEventos(it))
        }

        AdelaidaDialog(
            { }, DialogProperties(false, false, !isLandscape()),
            contentMustScroll = false, fillMaxHeight = true
        ) {
            ListadoEventos(eventos, eventoSeleccionado, ultimoVisto, { consumidor.consumir(SeleccionarEvento(it)) },
                { consumidor.consumir(RealizarEventoSeleccionado(it)) }, dismiss)
        }
    }
}

@Composable
private fun SeccionVisitaAdelaida() {
    Column {
        Titulo("Adelaida")
        Row(Modifier.padding(bottom = MargenEstandar), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdelaidaText("Rosita aún tiene que ser visitada por Adelaida", Modifier.weight(1f))
            AdelaidaButton({}) {
                AdelaidaText("VISITAR")
            }
        }
    }
}

@Composable
@Preview
private fun PreviewScreenNoche() {
    val eventos = eventosVo(7)
    val seleccionado = eventos[2]

    ScreenPreviewMarron {
        ScreenNoche(
            Estados(
                eventos = Estados.Estado.Eventos(eventos),
                eventoSeleccionado = Estados.Estado.EventoSeleccionado(seleccionado)
            ),
            Consumidor.Dummy,
            7L,
            { true },
        )
    }
}

@NightAndDay
@Composable
private fun EventosVerde() {
    val eventos = eventosVo(3)
    PreviewComponente {
        SeccionEvento(
            Estados(
                eventos = Estados.Estado.Eventos(eventos),
                eventoSeleccionado = Estados.Estado.EventoSeleccionado(eventos[1]),
                mostrarDialogoSeleccionManualEvento = Estados.Estado.MostrarDialogoSeleccionManualEvento(false, null),
            ),
            Consumidor.Dummy,
        )
    }
}

@NightAndDay
@Composable
private fun Visita() {
    PreviewComponente {
        SeccionVisitaAdelaida()
    }
}

@Composable
@NightAndDay
private fun P3() {
    PreviewComponente {
        val evento = eventos(1)[0]
        val jugadores = jugadores("Esaul", "Loberto", "Loreal", "Garnier", "Rosabella")
            .mapIndexed { index, jugador -> JugadorVO(jugador, index % 2 == 0) }
        val eventoRealizandose = EventoRealizandose(evento, jugadores, false)


        DialogoRealizacionEvento(
            Estados(eventoRealizandose = Estados.Estado.EventoRealizandose(eventoRealizandose)),
            Consumidor.Dummy,
        )
    }
}

class EstadoBaremos(
    val jugador: Jugador,
    val baremoSeleccionado: Baremo?,
    val baremos: List<Baremo>,
    val baremosNoSeleccionables: List<Baremo>
)