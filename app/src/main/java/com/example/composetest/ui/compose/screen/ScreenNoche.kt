package com.example.composetest.ui.compose.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.R
import com.example.composetest.extensions.get
import com.example.composetest.extensions.hasAtLeast
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.DialogoRealizacionEvento
import com.example.composetest.ui.compose.NightAndDay
import com.example.composetest.ui.compose.NombreJugadorEditable
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.sampledata.eventos
import com.example.composetest.ui.compose.sampledata.eventosVo
import com.example.composetest.ui.compose.sampledata.jugador
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
import com.example.composetest.ui.contracts.ConsumidorNoche
import com.example.composetest.ui.contracts.IntencionNoche
import com.example.composetest.ui.contracts.IntencionNoche.AbrirDialogoSeleccionManualEventos
import com.example.composetest.ui.contracts.IntencionNoche.GuardarBaremo
import com.example.composetest.ui.contracts.IntencionNoche.OcultarDialogoEjecucionEvento
import com.example.composetest.ui.contracts.IntencionNoche.RealizarEventoSeleccionado
import com.example.composetest.ui.contracts.IntencionNoche.SeleccionarEvento
import com.example.composetest.ui.contracts.IntencionNoche.SeleccionarEventoAleatorio
import com.example.composetest.ui.manager.InfoVisita
import com.example.composetest.ui.manager.ValidacionVisita
import com.example.composetest.ui.manager.puedeSerVisitado
import com.example.composetest.ui.manager.run
import com.example.composetest.ui.viewmodel.EstadoNoche
import com.example.composetest.ui.viewmodel.NocheViewModel
import com.example.composetest.ui.viewmodel.NocheViewModel.EventoRealizandose
import com.example.composetest.ui.viewmodel.NocheViewModel.JugadorVO

@Composable
fun TabEventos(partida: Partida?, onMensaje: (Mensaje) -> Unit) {
    ScreenNoche(
        partida = partida,
        cambioRondaSolicitado = false, // Esto no se pasa hasta aquí porque se gestiona en la ScreenPartida
        onCondicionesCambioRondaSatisfechas = {}, // Idem
        onMensaje = onMensaje
    )
}

@Composable
fun ScreenNoche(
    partida: Partida?,
    cambioRondaSolicitado: Boolean,
    onCondicionesCambioRondaSatisfechas: (Boolean) -> Unit,
    onMensaje: (Mensaje) -> Unit
) {
    val context = LocalContext.current
    val viewModel: NocheViewModel = hiltViewModel()
    viewModel.onMensaje = onMensaje
    viewModel.inicializar(partida, cambioRondaSolicitado, onCondicionesCambioRondaSatisfechas, context)

    Screen(
        viewModel.estados,
        viewModel.consumidor,
        partida?.id,
        viewModel::comprobarNombreRepetido,
    )
}

@Composable
private fun Screen(
    estado: EstadoNoche,
    consumidor: ConsumidorNoche,
    idPartida: Long?,
    comprobarNombreRepetido: (String) -> Boolean,
) {
    val scroll = rememberScrollState()
    Column(Modifier
        .fillMaxSize()
        .padding(horizontal = MargenEstandar)
        .verticalScroll(scroll)
    ) {
        SeccionBaremos(estado, consumidor, idPartida, comprobarNombreRepetido)
        AdelaidaDivider(Modifier.padding(top = 10.dp))
        SeccionEvento(estado, consumidor)
        AdelaidaDivider(Modifier.padding(top = 10.dp))
        SeccionVisitaAdelaida(estado, consumidor)
    }

    DialogoSeleccionBaremos(estado, consumidor)
    DialogoConfirmarEjecucionAhora(estado, consumidor)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun SeccionBaremos(
    estados: EstadoNoche,
    consumidor: ConsumidorNoche,
    idPartida: Long?,
    comprobarNombreRepetido: (String) -> Boolean
) {
    val jugadores by remember { estados.jugadores }
    Seccion(
        cabecera = { Titulo("Baremos") },
        contenido = {
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                jugadores.forEach {
                    Column(
                        Modifier
                            .clickable { consumidor.consumir(IntencionNoche.AbrirDialogoBaremos(it)) }
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
        })
}

@Composable
private fun DialogoSeleccionBaremos(estados: EstadoNoche, consumidor: ConsumidorNoche) {
    val estadoBaremos by remember { estados.estadoDialogoBaremos }
    val cerrarDialogoBaremos = { consumidor.consumir(IntencionNoche.CerrarBaremo()) }

    estadoBaremos?.let {
        AdelaidaDialog(cerrarDialogoBaremos, DialogProperties(true, true, false), contentMustScroll = false) {
            val state = rememberListadoBaremosState(it.baremoSeleccionado, it.baremosNoSeleccionables)
            ListadoBaremos(state, it.jugador, it.baremos, { jugador, baremo ->
                consumidor.consumir(GuardarBaremo(jugador, baremo)) }, cerrarDialogoBaremos)
        }
    }
}

@Composable
private fun DialogoConfirmarEjecucionAhora(estados: EstadoNoche, consumidor: ConsumidorNoche) {
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
private fun SeccionEvento(estado: EstadoNoche, consumidor: ConsumidorNoche) {
    val eventoSeleccionado by remember { estado.eventoSeleccionado }
    val mostrarDialogo by remember { estado.mostrarDialogoSeleccionManualEvento }
    val yaSeHaRealizado by remember { estado.yaSeHaRealizado }

    Seccion(
        cabecera = {
            Titulo("Evento")
            if (yaSeHaRealizado) {
                AdelaidaText("Ya realizado", Modifier.padding(start = 10.dp))
            }
        },
        contenido = { TituloEventoYBotones(estado, consumidor, eventoSeleccionado, yaSeHaRealizado) }
    )

    DialogoEventos(estado, consumidor, mostrarDialogo.mostrar, eventoSeleccionado, mostrarDialogo.ultimoEventoVisto)
    DialogoRealizacionEvento(estado, consumidor)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TituloEventoYBotones(
    estado: EstadoNoche,
    consumidor: ConsumidorNoche,
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
            Modifier
                .then(dimensiones)
                .padding(end = margenFin),
            TextAlign.Center.takeIf { !esLandscape } ?: TextAlign.Start
        )
        FlowRow(horizontalArrangement = Arrangement.End.takeIf { isLandscape() } ?: Arrangement.Center) {
            val medidaBoton = Modifier.fillMaxWidth().takeUnless { esLandscape } ?: Modifier.wrapContentWidth()

            if (!yaSeHaRealizado) {
                AdelaidaButton(seleccionEventoAleatorio,
                    stringResource(R.string.aleatorio), Modifier
                        .padding(end = margenFin)
                        .then(medidaBoton))
                AdelaidaButton(seleccionManual, stringResource(R.string.seleccion_manual), medidaBoton)

                if (eventoSeleccionado?.sePuedeEjecutarAhora == true) {
                    AdelaidaButton(realizarEvento,
                        stringResource(R.string.realizar_evento), Modifier
                            .padding(end = margenFin)
                            .then(medidaBoton))
                }
            }
        }
    }
}

@Composable
private fun DialogoEventos(
    estados: EstadoNoche,
    consumidor: ConsumidorNoche,
    mostrarDialogo: Boolean,
    eventoSeleccionado: EventoVO?,
    ultimoVisto: EventoVO?,
) {
    val eventos by remember { estados.eventos }

    if (mostrarDialogo) {
        val dismiss: (EventoVO) -> Unit = {
            consumidor.consumir(IntencionNoche.CerrarDialogoSeleccionManualEventos(it))
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
private fun SeccionVisitaAdelaida(estado: EstadoNoche, consumidor: ConsumidorNoche) {
    val visitas by remember { estado.visitaAdelaida }

    Seccion(
        {
            Titulo("Adelaida")

            if (visitas is InfoVisita.Cargando) {
                CircularProgressIndicator(Modifier
                    .size(30.dp)
                    .padding(start = 8.dp), Color.White)
            }
        },
        {
            when (visitas) {
                InfoVisita.Cargando -> { /* No mostrar nada mientras carga. */ }
                InfoVisita.NadieParaVisitar -> { AdelaidaText(R.string.nadie_a_quien_visitar.get()) }
                is InfoVisita.Info -> {
                    (visitas as InfoVisita.Info).list.forEach { info ->
                        Row(
                            Modifier.padding(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(Modifier.weight(1f)) {
                                val nombreNegrita = AnnotatedString.Range(
                                    SpanStyle(fontWeight = FontWeight.Bold), 0, info.jugador.nombre.length
                                )
                                AdelaidaText(getMensajeVisita(info), spans = listOf(nombreNegrita))
                            }

                            if (info.validaciones.run()) {
                                Box {
                                    AdelaidaButton({ consumidor.consumir(IntencionNoche.Visitar(info.jugador)) }) {
                                        AdelaidaText("VISITAR")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
}

@Composable
private fun getMensajeVisita(infoJugador: InfoVisita.Jugador): String {
    val puedeSerVisitado = infoJugador.validaciones.run()
    val mensaje: String = if (puedeSerVisitado) {
        "${infoJugador.jugador.nombre} tiene que ser visitado."
    } else {
        val fallidas = infoJugador.validaciones.filter { !it.validar() }
        val fallidasFinales = if (fallidas.any { it is ValidacionVisita.NoTieneElPerseskud }) {
            fallidas.filter { it is ValidacionVisita.NoTieneElPerseskud }
        } else {
            fallidas
        }
        fallidasFinales.map { visita ->
            when (visita) {
                is ValidacionVisita.TieneUnSecretoNuevo ->
                    R.string.jugador_sin_secretos_nuevos.get()

                is ValidacionVisita.TieneSuficientesCartas ->
                    R.string.jugador_sin_cartas_suficientes_para_visita.get()

                is ValidacionVisita.NoTieneElPerseskud ->
                    R.string.jugador_con_perseskud.get()
            }
        }.let { mensajes ->
            val append = if (mensajes.hasAtLeast(2)) {
                val listaRazones = mensajes.joinToString(separator = "") {
                    "\n  - ${it.capitalize(Locale.current)}."
                }
                ":$listaRazones"
            } else {
                " ${mensajes[0]}."
            }
            "${infoJugador.jugador.nombre} no puede ser visitado porque$append"
        }
    }

    return mensaje
}

@Composable
private fun Seccion(
    cabecera: @Composable RowScope.() -> Unit,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, content = cabecera)
        contenido()
    }
}

@Composable
@NightAndDay
private fun PreviewScreenNoche() {
    val eventos = eventosVo(7)
    val seleccionado = eventos[2]
    val jugadores = listOf(
        jugador("Secreto nuevo", pistas = mutableListOf(ElementoTablero.Pista.Secreto(2))),
        jugador(
            "Secreto nuevo pero sin cartas",
            pistas = mutableListOf(ElementoTablero.Pista.Secreto(2)),
            cartas = mutableListOf()
        ),
        jugador("Perseskud", cartas = mutableListOf(ElementoTablero.Carta.Perseskud())),
        jugador(
            "Sin secreto nuevo",
            pistas = mutableListOf(),
            cartas = mutableListOf(
                ElementoTablero.Carta.Dinero(2, 1000),
                ElementoTablero.Carta.Dinero(3, 1000),
            )
        )
    )

    ScreenPreviewVerde {
        Screen(
            EstadoNoche(
                eventos = EstadoNoche.Estado.Eventos(eventos),
                eventoSeleccionado = EstadoNoche.Estado.EventoSeleccionado(seleccionado),
                jugadores = EstadoNoche.Estado.Jugadores(jugadores),
                visitaAdelaidaInfo = EstadoNoche.Estado.VisitasAdelaida(
                    InfoVisita.Info(jugadores.map { InfoVisita.Jugador(it, puedeSerVisitado(it)) })
                )
            ),
            Consumidor.Dummy,
            7L,
            { true },
        )
    }
}

@Composable
@NightAndDay
private fun PreviewDialogoRealizacion() {
    PreviewFondo {
        val evento = eventos(1)[0]
        val jugadores = jugadores("Esaul", "Loberto", "Loreal", "Garnier", "Rosabella")
            .mapIndexed { index, jugador -> JugadorVO(jugador, index % 2 == 0) }
        val eventoRealizandose = EventoRealizandose(evento, jugadores, false)


        DialogoRealizacionEvento(
            EstadoNoche(eventoRealizandose = EstadoNoche.Estado.EventoRealizandose(eventoRealizandose)),
            Consumidor.Dummy,
        )
    }
}
