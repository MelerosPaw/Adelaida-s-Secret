package com.example.composetest.ui.compose.screen

import TabJugadores
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.composetest.R
import com.example.composetest.extensions.get
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.AccionProhibida
import com.example.composetest.ui.compose.BarraNavegacionPartida
import com.example.composetest.ui.compose.BotonFiltros
import com.example.composetest.ui.compose.Cargando
import com.example.composetest.ui.compose.DialogoAccionProhibida
import com.example.composetest.ui.compose.HtmlSpan
import com.example.composetest.ui.compose.NightAndDay
import com.example.composetest.ui.compose.Papelera
import com.example.composetest.ui.compose.TabData
import com.example.composetest.ui.compose.TabInfo
import com.example.composetest.ui.compose.TabTableroPartida
import com.example.composetest.ui.compose.getTabs
import com.example.composetest.ui.compose.modifiers.invisibleClick
import com.example.composetest.ui.compose.navegacion.CLAVE_CARGA_INICIAL
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.navegacion.NavegadorCreacion
import com.example.composetest.ui.compose.navegacion.NavegadorRondas
import com.example.composetest.ui.compose.sampledata.partidas
import com.example.composetest.ui.compose.theme.Explicacion
import com.example.composetest.ui.compose.theme.FondoPantallaRobo
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.AdelaidaDivider
import com.example.composetest.ui.compose.widget.AdelaidaIconButton
import com.example.composetest.ui.compose.widget.AdelaidaIconDefaults
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.contracts.ConsumidorPartida
import com.example.composetest.ui.contracts.ConsumidorTabInfo
import com.example.composetest.ui.contracts.EstadoPartida
import com.example.composetest.ui.contracts.IntencionPartida
import com.example.composetest.ui.contracts.IntencionTabInfo
import com.example.composetest.ui.manager.AsuntoTurbio
import com.example.composetest.ui.manager.AsuntoTurbio.Ninguno
import com.example.composetest.ui.viewmodel.PartidaViewModel
import com.example.composetest.ui.viewmodel.PartidaViewModel.InfoRonda
import kotlinx.coroutines.delay

@Composable
fun ScreenPartida(
    cambiarConfiguracionToolbar: (NavegadorCreacion.ConfiguracionToolbar) -> Unit,
    onAbandonar: () -> Unit,
    onMensaje: (Mensaje) -> Unit
) {
    val viewModel: PartidaViewModel = hiltViewModel()
    viewModel.onMensaje = onMensaje
    viewModel.onAbandonar = onAbandonar

    LaunchedEffect(CLAVE_CARGA_INICIAL) {
        viewModel.cargarPartida()
    }

    ScreenPartida(
        cambiarConfiguracionToolbar,
        viewModel.consumidor,
        viewModel.estado,
        viewModel.infoRonda,
        viewModel::onCambioRondaSolicitado,
        viewModel::onMostrarDialogoCambioRonda,
        viewModel::onCambiarVisibilidadExplicacionRonda,
        viewModel::onCerrarTituloSiguienteRonda,
        viewModel::onNavegacionHabilitada,
        viewModel::actualizarNombrePartida,
        viewModel::cancelarCompra,
        viewModel.filtrosAbiertos,
        viewModel::abrirFiltros,
        viewModel::cerrarFiltros,
        viewModel.mostrarIconoPapelera,
        viewModel::onMostrarIconoPapelera,
        viewModel::abrirPapelera,
        viewModel.mostrarPapelera,
        viewModel::cerrarPapelera,
        viewModel::recuperarElemento,
        viewModel::onRondaSiguiente,
        viewModel.mostrarDialogoAbandonar,
        viewModel::onMostrarDialogoAbandonarCambiado,
        viewModel::abandonar,
    )
}

@Composable
private fun ScreenPartida(
    cambiarConfiguracionToolbar: (NavegadorCreacion.ConfiguracionToolbar) -> Unit,
    consumidor: ConsumidorPartida,
    estado: EstadoPartida,
    infoRonda: State<InfoRonda?>,
    onCambioRondaSolicitado: (InfoRonda) -> Unit,
    onMostrarDialogoCambioRonda: (InfoRonda) -> Unit,
    onCambiarVisibilidadInfoRonda: (InfoRonda) -> Unit,
    onOcultarTituloSiguienteRonda: (InfoRonda) -> Unit,
    onNavegacionHabilitada: (NavegadorRondas) -> Unit,
    actualizarNombrePartida: (String) -> Unit,
    cancelarCompra: () -> Unit,
    filtrosAbiertos: State<Boolean>,
    abrirFiltros: () -> Unit,
    cerrarFiltros: () -> Unit,
    mostrarIconoPapelera: State<Boolean>,
    onMostrarPapelera: (Boolean) -> Unit,
    abrirPapelera: () -> Unit,
    papeleraAbierta: State<Boolean>,
    cerrarPapelera: () -> Unit,
    recuperarElemento: (elemento: ElementoTablero, jugador: Jugador) -> Unit,
    onRondaSiguiente: () -> Unit,
    mostrarDialogoSalir: State<Boolean>,
    onMostrarMensajeAbandonar: (Boolean) -> Unit,
    onSalir: () -> Unit,
) {
    val partida by remember { estado.partida }
    val info by remember { infoRonda }
    val tabActual by remember { estado.tabActual }
    val asuntoTurbio by remember { estado.asuntoTurbio }
    val accionProhibida by remember { estado.infoAccionProhibida }

    // Muestra un toast con los jugadores que tienen comodines y el evento actual
//    partida?.let {
//        val nombreEvento = it.eventoActual?.nombre ?: "Sin evento actual"
//        val jugadoresConComodines: String = it.jugadores.mapNotNull { jugador ->
//            val comodines = jugador.comodines.takeUnless { it.isEmpty() }
//            val comodinesUnidos = comodines?.joinToStringHumanReadable { it.nombre }
//            comodinesUnidos?.let { "${jugador.nombre } tiene $it" }
//        }.takeIf { it.isNotEmpty() }
//            ?.joinToStringHumanReadable { "$it\n" } ?: "Nadie tiene comodines"
//        val mensaje = "Partida: $nombreEvento\n$jugadoresConComodines"
//
//        Toast.makeText(LocalContext.current, mensaje, Toast.LENGTH_SHORT).show()
//    }

    Screen(
        configuracionToolbar = NavegadorCreacion.ConfiguracionToolbar(
            titulo = NavegadorCreacion.ConfiguracionToolbar.nombrePartida(
                partida.partida?.nombre.orEmpty(),
                actualizarNombrePartida,
                asuntoTurbio.asuntoTurbio is Ninguno
            ),
            actions = {
                Box {
                    AbrirFiltros(abrirFiltros)
                    AbrirPapelera(
                        mostrarIconoPapelera, partida.partida?.jugadores?.toList(), papeleraAbierta,
                        abrirPapelera, cerrarPapelera, recuperarElemento
                    )
                }
                CancelarRobo(asuntoTurbio.asuntoTurbio !is Ninguno, cancelarCompra)
                RondaSiguiente(
                    info,
                    onCambioRondaSolicitado,
                    onMostrarDialogoCambioRonda
                ) { onRondaSiguiente() }
            }
        )) {

        val colorFondo = FondoPantallaRobo.takeIf { asuntoTurbio.asuntoTurbio !is Ninguno }
            ?: Tema.colors.fondoPantalla

        Box(Modifier.fillMaxSize(), propagateMinConstraints = true) {
            Column(Modifier.background(colorFondo)) {
                BackHandler { onMostrarMensajeAbandonar(true) }
                InfoRonda(info, onCambiarVisibilidadInfoRonda)
//                VisorPartida(
//                    partida, onMensaje, filtrosAbiertos, cerrarFiltros, onMostrarPapelera,
//                    onNavegacionHabilitada, info?.solicitarCambioRonda == true,
//                    onCondicionesCambioRondaSatisfechas
//                )
                Contenido(estado, consumidor, asuntoTurbio.asuntoTurbio, filtrosAbiertos,
                    cerrarFiltros, {}
                )
                BarraNavegacionPartida(
                    tabSeleccionada = tabActual.tab,
                    tabs = getTabs { consumidor.consumir(IntencionPartida.CambiarTab(it)) }
                )
            }

            TituloInicioRonda(info, onOcultarTituloSiguienteRonda)
            DialogoAccionProhibida(accionProhibida.estado)
            DialogoSalir(mostrarDialogoSalir, onMostrarMensajeAbandonar, onSalir)
        }
    }
}

@Composable
private fun ColumnScope.Contenido(
    estado: EstadoPartida,
    consumidor: ConsumidorPartida,
    asuntoTurbio: AsuntoTurbio,
    filtrosAbiertos: State<Boolean>,
    cerrarFiltros: () -> Unit,
    onAccionProhibida: (AccionProhibida) -> Unit, // Mover muchas cosas
) {
    val partida by remember { estado.partida }
    val tabActual by remember { estado.tabActual }
    val estadoTablero by remember { estado.estadoTablero }
    val jugadores = partida.partida?.jugadores?.toList()

    Box(Modifier.weight(1f)) {
        when {
            tabActual.tab == null || partida.partida == null -> CargandoPartida()
            tabActual.tab == TabData.TABLERO -> TabTableroPartida(
                filtrosAbiertos, estadoTablero.estadoTablero, jugadores, partida.partida?.id,
                cerrarFiltros, onAccionProhibida
            )
            tabActual.tab == TabData.JUGADORES -> TabJugadores(
                asuntoTurbio, partida.partida?.id, jugadores,
                false, // TODO Melero: 6/7/25 Se tiene que poder alternar
                { consumidor.consumir(IntencionPartida.IniciarAsuntoTurbio(it)) },
                onAccionProhibida
            )
            tabActual.tab == TabData.EVENTOS -> TabEventos(partida.partida) {
                consumidor.consumir(IntencionPartida.MostrarMensaje(it))
            }

            tabActual.tab == TabData.INFO -> {
                val tabInfo by remember { estado.estadoTabInfo }
                TabInfo(tabInfo, object : ConsumidorTabInfo {
                    override fun consumir(vararg intenciones: IntencionTabInfo) {
                        intenciones.forEach {
                            when (it) {
                                IntencionTabInfo.MostrarDialogoSalir -> consumidor.consumir(
                                    IntencionPartida.MostrarDialogoAbandonar
                                )

                                is IntencionTabInfo.MostrarMensaje -> consumidor.consumir(
                                    IntencionPartida.MostrarMensaje(it.mensaje)
                                )
                            }
                        }
                    }
                })
            }
        }
    }
}

@Composable
private fun CargandoPartida() {
    Box(Modifier.fillMaxSize(), propagateMinConstraints = true) {
        Cargando()
    }
}

@Composable
private fun DialogoSalir(
    mostrarDialogoSalir: State<Boolean>,
    onMostrarDialogoSalirCambiado: (Boolean) -> Unit,
    onSalir: () -> Unit
) {
    val mostrar by remember { mostrarDialogoSalir }

    if (mostrar) {
        AdelaidaButtonDialog("¿Quieres abandonar la partida?",
            arrayOf(
                OpcionDialogo("Sí", null) { onSalir() },
                OpcionDialogo("No", null) { onMostrarDialogoSalirCambiado(false) },
            )
        )
    }
}

@Composable
private fun AbrirFiltros(abrirFiltros: () -> Unit) {
    BotonFiltros(Tema.colors.toolbarContent, abrirFiltros)
}

@Composable
private fun AbrirPapelera(
    mostrar: State<Boolean>,
    jugadores: List<Jugador>?,
    papeleraAbierta: State<Boolean>,
    abrirPapelera: () -> Unit,
    cerrarPapelera: () -> Unit,
    recuperarElemento: (elemento: ElementoTablero, jugador: Jugador) -> Unit
) {
    val mostrar by remember { mostrar }

    if (mostrar) {
        jugadores
            ?.filter { it.gastadas().isNotEmpty() }
            ?.let {
                BotonPapelera(Tema.colors.toolbarContent) { abrirPapelera() }

                val mostrarPapelera by remember { papeleraAbierta }
                if (mostrarPapelera) {
                    Papelera(it, cerrarPapelera, recuperarElemento)
                }
            }
    }
}

@Composable
private fun BotonPapelera(tint: Color = AdelaidaIconDefaults.tint, onClicked: () -> Unit) {
    AdelaidaIconButton(Icons.Filled.Delete, "Papelera", onClick = onClicked, tint = tint)
}

@Composable
private fun RondaSiguiente(
    infoRonda: InfoRonda?,
    onCambioRondaSolicitado: (InfoRonda) -> Unit,
    onMostrarDialogoCambioRonda: (InfoRonda) -> Unit,
    color: Color = Tema.colors.toolbarContent,
    onCambiarRonda: () -> Unit,
) {
    infoRonda?.let {
        AdelaidaIconButton(Icons.Default.AccessTime, "Avanzar a la siguiente ronda", tint = color) {
            onCambioRondaSolicitado(it)
        }

        if (it.mostrarDialogoSiguienteRonda) {
            AdelaidaButtonDialog(
                it.preguntaSiguienteRonda.get(),
                arrayOf(
                    OpcionDialogo("Sí", null) {
                        onMostrarDialogoCambioRonda(infoRonda)
                        onCambiarRonda()
                    },
                    OpcionDialogo("No", null) { onMostrarDialogoCambioRonda(infoRonda) },
                )
            )
        }
    }
}

@Composable
private fun CancelarRobo(mostrar: Boolean, cancelarCompra: () -> Unit) {
    if (mostrar) {
        AdelaidaIconButton(Icons.Default.Close, "Dejar de comprar", tint = Tema.colors.toolbarContent) { cancelarCompra() }
    }
}

@Composable
private fun InfoRonda(
    infoRonda: InfoRonda?,
    onCambiarVisibilidadInfoRonda: (InfoRonda) -> Unit,
) {
    val horizontalPadding = MargenEstandar
    val colorTexto = Tema.colors.toolbarContent

    infoRonda?.let { info ->
        Column(
            Modifier
                .fillMaxWidth()
                .background(Explicacion)
                .clickable { onCambiarVisibilidadInfoRonda(info) }
                .padding(horizontal = horizontalPadding)
        ) {
            val paddingValue = with(LocalDensity.current) {
                horizontalPadding.roundToPx()
            }
            AnimatedContent(info.explicacionVisible, label = "Nombre de la ronda",
                transitionSpec = {
                    val entrada = slideInHorizontally(
                        initialOffsetX = { fullWidth ->
                            if (targetState) {
                                (fullWidth / 2) - paddingValue * 3
                            } else {
                                (-fullWidth / 2) + paddingValue * 3
                            }
                        },
                        animationSpec = tween(durationMillis = 700)
                    )
                    
                    entrada togetherWith fadeOut(animationSpec = tween(durationMillis = 0))
                }
            ) { visible ->
                Column(Modifier.fillMaxWidth()) {
                    val alignment = Modifier.align(Alignment.Start).takeIf { visible }
                        ?: Modifier.align(Alignment.CenterHorizontally)
                    val contentDescription = "Ocultar la explicación de la ronda".takeIf { visible }
                        ?: "Ver explicación de la ronda"

                    Row(modifier = alignment, verticalAlignment = Alignment.CenterVertically) {
                        Titulo(info.ronda.id.uppercase(), nivel = NivelTitulo.Nivel3, color = colorTexto)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp.takeIf { visible } ?: Icons.Default.KeyboardArrowDown,
                            contentDescription,
                            tint = colorTexto
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = info.explicacionVisible,
                enter = expandVertically(animationSpec = tween(durationMillis = 700)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 700))
            ) {
                Column(Modifier.padding(bottom = 8.dp)) {
                    info.subtitulo?.let {
                        Titulo(it.get(), nivel = NivelTitulo.Nivel2, color = colorTexto)
                    }

                    if (infoRonda.explicacionEsHTMTL) {
                        val html = HtmlSpan(info.explicacion.get())
                        AdelaidaText(html.text, spans = html.spans, color = colorTexto)
                    } else {
                        AdelaidaText(info.explicacion.get(), color = colorTexto)
                    }
                }
            }
        }
    }
}

@Composable
private fun TituloInicioRonda(
    info: InfoRonda?,
    onOcultarTituloSiguienteRonda: (InfoRonda) -> Unit
) {
    info?.let {
        AnimatedVisibility(
            it.mostrarTituloSiguienteRonda,
            enter = fadeIn(animationSpec = tween(1300)),
            exit = fadeOut(animationSpec = tween(2300))
        ) {
            ContenidoTituloRonda(info, onOcultarTituloSiguienteRonda)
        }
    }
}

@Composable
private fun ContenidoTituloRonda(
    info: InfoRonda,
    onOcultarTituloSiguienteRonda: (InfoRonda) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Tema.colors.fondoDialogos)
            /* Intercepta los clics para que no se pueda pulsar nada mientras se desvanece. */
            .invisibleClick { },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Titulo(info.ronda.id,
                Modifier.padding(bottom = 55.dp),
                nivel = NivelTitulo.Pantalla)
            AdelaidaDivider(Modifier.padding(horizontal = 45.dp))
            Titulo(
                "Día ${info.dia}",
                Modifier.padding(top = 65.dp),
                nivel = NivelTitulo.SubtituloPantalla
            )
        }
        LaunchedEffect("Cambio de ronda") {
            delay(1300L)
            onOcultarTituloSiguienteRonda(info)
        }
    }
}

@Composable
private fun ColumnScope.VisorPartida(
    partida: Partida?,
    onMensaje: (Mensaje) -> Unit,
    filtrosAbiertos: State<Boolean>,
    onCerrarFiltros: () -> Unit,
    onMostrarPapelera: (Boolean) -> Unit,
    onNavegacionHabilitada: (NavegadorRondas) -> Unit,
    cambioRondaSolicitado: Boolean,
    onCondicionesCambioRondaSatisfechas: (Boolean) -> Unit,
    onMostrarMensajeAbandonar: () -> Unit,
) {
    partida?.let {
        NavegadorRondas.obtenerDestinoDeRondaActual(it.ronda)?.let {
            val navHostController = rememberNavController()
            val grafoRondas = NavegadorRondas.grafo(navHostController, partida, onMensaje,
                filtrosAbiertos, onCerrarFiltros, onMostrarPapelera, cambioRondaSolicitado,
                onCondicionesCambioRondaSatisfechas, onMostrarMensajeAbandonar)
            onNavegacionHabilitada(NavegadorRondas(navHostController))

            NavHost(
                navHostController,
                startDestination = it,
                builder = grafoRondas,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun ScreenPartidaPreview() {
    val partida = partidas(1)[0]
    val estado = EstadoPartida()
    estado.setPartida(partida)

    val viewModel = PartidaViewModel(SavedStateHandle())
    val infoRonda = InfoRonda(
        partida.ronda,
        12,
        false, viewModel.getPreguntaSiguienteRonda(partida.ronda),
        false,
        viewModel.getSubtitulo(partida.ronda),
        viewModel.getExplicacion(partida.ronda),
        viewModel.getExplicacionEsHtml(partida.ronda),
        true,
        false
    )

    ScreenPartida(
        {},
        ConsumidorPartida.Dummy(),
        estado,
        mutableStateOf(infoRonda),
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        mutableStateOf(false),
        {},
        {},
        mutableStateOf(false),
        {},
        {},
        mutableStateOf(false),
        {},
        { _, _ -> },
        {},
        mutableStateOf(false),
        {},
        {},
    )
}

@NightAndDay
@Composable
private fun PreviewTituloInicioRonda() {
    Screen {
        ContenidoTituloRonda(
            InfoRonda(
                Partida.Ronda.NOCHE,
                3,
                false,
                R.string.advertencia_reasignacion_pista_mediodia,
                true,
                R.string.alias_ronda_mediodia,
                R.string.alias_ronda_mediodia,
                false,
                false,
                false
            ), {}
        )
    }
}