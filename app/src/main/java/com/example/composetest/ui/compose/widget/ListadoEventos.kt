package com.example.composetest.ui.compose.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.composetest.R
import com.example.composetest.extensions.get
import com.example.composetest.model.Evento
import com.example.composetest.ui.compose.Equis
import com.example.composetest.ui.compose.NightAndDay
import com.example.composetest.ui.compose.sampledata.eventosVo
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.theme.TextoInhabilitadoClaro
import com.example.composetest.ui.compose.widget.dialog.AdelaidaDialogOutlinedButton
import com.example.composetest.ui.compose.widget.dialog.AdelaidaDialogOutlinedButtonColors
import com.example.composetest.ui.compose.widget.dialog.AdelaidaDialogOutlinedButtonDefaults
import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.launch

@Composable
fun ListadoEventos(
    eventos: List<EventoVO>,
    seleccionado: EventoVO?,
    ultimoVisto: EventoVO?,
    onEventoSeleccionado: (EventoVO) -> Unit,
    onRealizarEventoSeleccionado: (EventoVO) -> Unit,
    onCerrarListado: (ultimoEventoAbierto: EventoVO) -> Unit,
) {
    if (eventos.isNotEmpty()) {
        val colorElementos = TituloDefaults.textColor
        val pagerState = rememberPagerState(
            pageCount = { eventos.size },
            initialPage = (seleccionado ?: ultimoVisto)
                ?.let(eventos::indexOf)
                ?.takeIf { it != -1 }
                ?: 0
        )
        val currentPage by remember { derivedStateOf { pagerState.currentPage } }

        Column(Modifier.fillMaxHeight()) {
            TituloVentana({ onCerrarListado(eventos[currentPage]) })
            HorizontalPager(pagerState, Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                val vo = eventos[it]
                val estaSeleccionado = vo == seleccionado
                val colorTexto = colorElementos.takeIf { vo.seleccionable } ?: TextoInhabilitadoClaro
                val colorSeleccion = Tema.colors.resalte.takeIf { estaSeleccionado } ?: Color.Transparent
                val colorTextoFinal = colorSeleccion.takeIf { estaSeleccionado } ?: colorTexto

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    ResalteAnimado(Tema.colors.resalte, estaSeleccionado) {
//                        Titulo(
//                            vo.evento.nombre,
//                            Modifier.padding(horizontal = 8.dp),
//                            nivel = NivelTitulo.Nivel2,
//                            color = colorTextoFinal
//                        )
//                    }
                    Encabezado(vo.evento.nombre, colorTextoFinal, estaSeleccionado)
                    Explicacion(vo.evento, colorTextoFinal)
                }
            }

            HorizontalPagerIndicator(
                pagerState, eventos.size,
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = MargenEstandar)
                    .padding(top = MargenEstandar),
                activeColor = colorElementos
            )
            val evento = eventos[currentPage]

            BotoneraEventos(pagerState, eventos, evento, seleccionado, colorElementos,
                onEventoSeleccionado, onRealizarEventoSeleccionado)
        }
    }
}

@Composable
private fun TituloVentana(onCerrarListado: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Titulo("Selecciona un evento", Modifier.weight(1f), NivelTitulo.Nivel2, TextAlign.Center)
        Equis(onCerrarListado, "Cerrar")
    }
}

@Composable
fun Encabezado(
    nombreEvento: String,
    colorTextoFinal: Color = NivelTitulo.Nivel1.color,
    estaSeleccionado: Boolean = false
) {
    val tint = ColorFilter.tint(colorTextoFinal)

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        AnimatedVisibility(estaSeleccionado) {
            Image(painterResource(R.drawable.bullet_point), null, colorFilter = tint)
        }
        TituloEvento(nombreEvento, colorTextoFinal)
        AnimatedVisibility(estaSeleccionado) {
            Image(painterResource(R.drawable.bullet_point_inverted), null, colorFilter = tint)
        }
    }
}

@Composable
fun Explicacion(
    evento: Evento,
    colorTextoFinal: Color,
    mustScroll: Boolean = true,
) {
    val scrollableModifier = if (mustScroll) {
        val scrollState = rememberScrollState()
        Modifier.verticalScroll(scrollState)
    } else {
        Modifier
    }

    AdelaidaText(
        evento.explicacion,
        Modifier
            .padding(top = 8.dp)
            .then(scrollableModifier), color = colorTextoFinal
    )
}

@Composable
fun TituloEvento(
    nombreEvento: String?,
    colorTextoFinal: Color,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    Titulo(
        nombreEvento ?: R.string.sin_evento_seleccionado.get(),
        Modifier
            .padding(horizontal = 8.dp)
            .then(modifier),
        nivel = NivelTitulo.Nivel3,
        color = colorTextoFinal,
        textAlign = textAlign
    )
}

@Composable
fun ColumnScope.BotoneraEventos(
    pagerState: PagerState,
    eventos: List<EventoVO>,
    eventoActual: EventoVO,
    eventoSeleccionado: EventoVO?,
    colorBotones: Color,
    onEventoSeleccionado: (EventoVO) -> Unit,
    onRealizarEventoSeleccionado: (EventoVO) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .align(Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
    ) {
        BotonPaginaAnterior(colorBotones, pagerState, eventos.size)
        BotonSeleccionar(eventoActual, eventoSeleccionado, colorBotones, onEventoSeleccionado,
            onRealizarEventoSeleccionado)
        BotonPaginaSiguiente(colorBotones, pagerState, eventos.size)
    }
}

@Composable
fun BotonPaginaAnterior(colorBotones: Color, pagerState: PagerState, maxItems: Int,
                        conTexto: Boolean = false, reiniciar: Boolean = true, texto: String? = null,
                        mantenerHabilitado: Boolean = false, onClick: (() -> Unit)? = null) {
    val coroutineScope = rememberCoroutineScope()

    fun estaEnElLimite(): Boolean = pagerState.currentPage == 0

    fun calcularPaginaAnterior(): Int? = if (estaEnElLimite()) {
        (maxItems - 1).takeIf { reiniciar }
    } else {
        pagerState.currentPage.dec()
    }

    fun cambiarPagina() {
        coroutineScope.launch {
            calcularPaginaAnterior()?.let {
                pagerState.animateScrollToPage(it)
            }
        }
    }

    val enabled = mantenerHabilitado || !estaEnElLimite() || reiniciar
    val color = colorBotones.takeIf { enabled } ?: Tema.colors.contenidoDialogosInhabilitado
    val padding = PaddingValues(top = 0.dp, bottom = 0.dp, start = 2.dp, end = 2.dp).takeUnless { conTexto }
        ?: AdelaidaDialogOutlinedButtonDefaults.contentPadding()

    AdelaidaDialogOutlinedButton(
        { onClick?.invoke() ?: cambiarPagina() },
        Modifier.width(40.dp).takeUnless { conTexto } ?: Modifier,
        enabled = enabled,
        contentPadding = padding) {
        if (conTexto) {
            AdelaidaText(texto ?: "ANTERIOR", color = color)
        } else {
            AdelaidaIcon(
                Icons.AutoMirrored.Default.KeyboardArrowLeft,
                "Evento anterior",
                tint = colorBotones
            )
        }
    }
}

@Composable
fun BotonPaginaSiguiente(colorBoton: Color, pagerState: PagerState, maxItems: Int,
                         conTexto: Boolean = false, reiniciar: Boolean = true, texto: String? = null,
                         mantenerHabilitado: Boolean = false, onClick: (() -> Unit)? = null) {
    val coroutineScope = rememberCoroutineScope()

    fun estaEnElLimite(): Boolean = pagerState.currentPage == maxItems - 1

    fun calcularPaginaSiguiente(): Int? = if (estaEnElLimite()) {
        0.takeIf { reiniciar }
    } else {
        pagerState.currentPage.inc()
    }

    fun cambiarPagina() {
        coroutineScope.launch {
            calcularPaginaSiguiente()?.let {
                pagerState.animateScrollToPage(it)
            }
        }
    }

    val enabled = mantenerHabilitado || !estaEnElLimite() || reiniciar
    val color = colorBoton.takeIf { enabled } ?: Tema.colors.contenidoDialogosInhabilitado
    val padding = PaddingValues(top = 0.dp, bottom = 0.dp, start = 2.dp, end = 2.dp).takeUnless { conTexto }
        ?: AdelaidaDialogOutlinedButtonDefaults.contentPadding()

    AdelaidaDialogOutlinedButton(
        { onClick?.invoke() ?: cambiarPagina() },
        Modifier.width(40.dp).takeUnless { conTexto } ?: Modifier,
        enabled = enabled,
        contentPadding = padding
    ) {
        if (conTexto) {
            AdelaidaText(texto ?: "SIGUIENTE", color = color)
        } else {
            AdelaidaIcon(
                Icons.AutoMirrored.Default.KeyboardArrowRight,
                "Evento siguiente",
                tint = colorBoton
            )
        }
    }
}

@Composable
private fun RowScope.BotonSeleccionar(
    eventoActual: EventoVO,
    eventoSeleccionado: EventoVO?,
    colorBotones: Color,
    onEventoSeleccionado: (EventoVO) -> Unit,
    onRealizarEventoSeleccionado: (EventoVO) -> Unit
) {
    val esElSeleccionado = eventoActual == eventoSeleccionado
    val esSeleccionable = eventoActual.seleccionable
    val sePuedeEjecutarAhora = eventoActual.sePuedeEjecutarAhora

    val colorBoton = getColorBoton(esElSeleccionado, esSeleccionable, colorBotones)

    AdelaidaDialogOutlinedButton(
        onClick = getOnClick(esElSeleccionado, sePuedeEjecutarAhora, onRealizarEventoSeleccionado,
            esSeleccionable, onEventoSeleccionado, eventoActual),
        modifier = Modifier.weight(1f),
        enabled = esSeleccionable || esElSeleccionado,
        colors = AdelaidaDialogOutlinedButtonColors(AdelaidaDialogOutlinedButtonDefaults.buttonColors, colorBoton)
    ) {
        AdelaidaButtonText(
            text = getTextoBoton(esElSeleccionado, sePuedeEjecutarAhora, esSeleccionable),
            fontWeight = getFontWeight(esElSeleccionado, esSeleccionable),
            color = colorBoton
        )
    }
}

@Composable
private fun getTextoBoton(
    esElSeleccionado: Boolean,
    sePuedeEjecutarAhora: Boolean,
    esSeleccionable: Boolean
): String = when {
    esElSeleccionado && sePuedeEjecutarAhora -> "LLEVAR A CABO"
    esElSeleccionado -> "SELECCIONADO"
    esSeleccionable -> "SELECCIONAR"
    else -> "USADO"
}

@Composable
private fun getOnClick(
    esElSeleccionado: Boolean,
    sePuedeEjecutarAhora: Boolean,
    onRealizarEventoSeleccionado: (EventoVO) -> Unit,
    esSeleccionable: Boolean,
    onEventoSeleccionado: (EventoVO) -> Unit,
    eventoActual: EventoVO
): () -> Unit = when {
    esElSeleccionado && sePuedeEjecutarAhora -> ({ onRealizarEventoSeleccionado(eventoActual) })
    esElSeleccionado || esSeleccionable -> ({ onEventoSeleccionado(eventoActual) })
    else -> ({})
}

@Composable
private fun getColorBoton(esElSeleccionado: Boolean, esSeleccionable: Boolean, colorBotones: Color): Color = when {
    esElSeleccionado -> Tema.colors.resalte
    esSeleccionable -> colorBotones
    else -> Tema.colors.contenidoDialogosInhabilitado
}

@Composable
private fun getFontWeight(esElSeleccionado: Boolean, esSeleccionable: Boolean): FontWeight = when {
    esElSeleccionado && esSeleccionable -> FontWeight.Black
    esElSeleccionado -> FontWeight.Bold
    else -> FontWeight.Normal
}

class EventoVO(val evento: Evento, val seleccionable: Boolean, val sePuedeEjecutarAhora: Boolean) {
    override fun toString(): String = evento.nombre

    override fun equals(other: Any?): Boolean = (other as? EventoVO)?.evento == evento
}

@NightAndDay
@Composable
private fun Eventos() {
    val eventos = eventosVo(3)
    ScreenPreviewMarron {
        ListadoEventos(eventos, eventos[1], eventos[3], {}, {}, {})
    }
}