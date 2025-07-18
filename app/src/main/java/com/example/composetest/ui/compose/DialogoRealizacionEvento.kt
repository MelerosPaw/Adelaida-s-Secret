package com.example.composetest.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.composetest.extensions.joinToStringHumanReadable
import com.example.composetest.model.Evento
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.compose.widget.BotonPaginaAnterior
import com.example.composetest.ui.compose.widget.BotonPaginaSiguiente
import com.example.composetest.ui.compose.widget.DialogTextCheckbox
import com.example.composetest.ui.compose.widget.Encabezado
import com.example.composetest.ui.compose.widget.Explicacion
import com.example.composetest.ui.compose.widget.dialog.AdelaidaDialog
import com.example.composetest.ui.compose.widget.dialog.AdelaidaDialogOutlinedButton
import com.example.composetest.ui.contracts.Consumidor
import com.example.composetest.ui.contracts.Intencion
import com.example.composetest.ui.contracts.Intencion.MarcarGanadorEvento
import com.example.composetest.ui.viewmodel.Estados
import com.example.composetest.ui.viewmodel.NocheViewModel.EventoRealizandose
import com.example.composetest.ui.viewmodel.NocheViewModel.JugadorVO

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DialogoRealizacionEvento(estados: Estados, consumidor: Consumidor) {
  val eventoRealizandose by remember { estados.eventoRealizandose }

  eventoRealizandose?.let { evento ->
    AdelaidaDialog({},
      DialogProperties(false, false, false),
      contentMustScroll = false,
      fillMaxHeight = true,
    ) {
      val colorTexto = Tema.colors.contenidoDialogos
      val seleccionados = evento.jugadores.filter { it.seleccionado }
      val haySeleccionados = seleccionados.isNotEmpty()
      val cantidadPaginas = 3.takeIf { haySeleccionados } ?: 2
      val pagerState = rememberPagerState { cantidadPaginas }
      val currentPage by remember { derivedStateOf { pagerState.currentPage } }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(pagerState, Modifier
          .fillMaxWidth()
          .weight(1f), verticalAlignment = Alignment.Top
        ) {
          when(it) {
            0 -> ExplicacionEvento(evento, colorTexto)
            1 -> SeleccionJugadoresAfectados(evento, colorTexto, consumidor)
            else -> ResumenEvento(seleccionados, evento)
          }
        }

        BotonesDialogoRealizacion(consumidor, currentPage, evento, colorTexto,
          pagerState, cantidadPaginas)
      }
    }
  }
}

@Composable
private fun ExplicacionEvento(evento: EventoRealizandose, colorTexto: Color) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Encabezado(evento.evento.nombre, colorTexto, true)

    val scrollState = rememberScrollState()
    Column(Modifier.verticalScroll(scrollState)) {
      Explicacion(evento.evento, colorTexto, false)
      AdelaidaText(
        """Pulsa "SIGUIENTE"""", Modifier.padding(top = MargenEstandar),
        fontStyle = FontStyle.Italic
      )
    }
  }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun SeleccionJugadoresAfectados(
  evento: EventoRealizandose,
  colorTexto: Color,
  consumidor: Consumidor
) {
  Column {
    val cantidadGanadores = evento.evento.maxGanadores.obtenerCantidadGanadores()
    val encabezadoPregunta = "Qué jugadores han sacado"
      .takeIf { cantidadGanadores > 1 }
      ?: "Qué jugador ha sacado"
    val puntuaciones = evento.evento.puntuaciones
    val jugadoresMaximos = cantidadGanadores.coerceAtMost(evento.jugadores.size)
    val pregunta = "¿$encabezadoPregunta $puntuaciones? (Máximo: $jugadoresMaximos)"

    Encabezado("Jugadores afectados", colorTexto)
    AdelaidaText(pregunta)
    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
      evento.jugadores.forEach { vo ->
        Row(
          modifier = Modifier.padding(top = MargenEstandar).padding(horizontal = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          val habilitado = vo.seleccionado || evento.puedeSeleccionarMasJugadores
          DialogTextCheckbox(
            vo.jugador.nombre, vo.seleccionado,
            { consumidor.consumir(MarcarGanadorEvento(it, vo)) },
            habilitado
          )
        }
      }
    }

    BotonDesempate(
      { consumidor.consumir(Intencion.MostrarMensaje(it)) },
      Modifier.align(Alignment.CenterHorizontally)
    )
  }
}

@Composable
private fun ResumenEvento(seleccionados: List<JugadorVO>, evento: EventoRealizandose) {
  if (seleccionados.isEmpty()) {
    AdelaidaText("Indica quiénes han ganado el evento para ver esta información.")
  } else {
    val texto = when (evento.evento.accion) {
      is Evento.Accion.OtorgarComodin -> {
        val obtiene = "obtienen".takeIf { seleccionados.size > 1 } ?: "obtiene"
        seleccionados.joinToStringHumanReadable { it.jugador.nombre } + " $obtiene un comodín " + evento.evento.accion.comodin.nombre
      }

      is Evento.Accion.AplicarEfecto -> evento.evento.accion.efecto.explicacion
    }

    AdelaidaText(texto)
  }
}

@Composable
private fun BotonesDialogoRealizacion(
  consumidor: Consumidor,
  currentPage: Int,
  evento: EventoRealizandose,
  colorTexto: Color,
  pagerState: PagerState,
  cantidadPaginas: Int
) {
  val esLaUltimaPagina = currentPage == 2
  val esLaPrimeraPagina = currentPage == 0
  val textoBotonSiguiente = "FIN DEL EVENTO".takeIf { esLaUltimaPagina }
  val textoBotonAnterior = "CANCELAR".takeIf { esLaPrimeraPagina }
  val clicSiguiente = { consumidor.consumir(Intencion.DarEventoPorRealizado(evento)) }
    .takeIf { esLaUltimaPagina }
  val clicAnterior = { consumidor.consumir(Intencion.CerrarRealizacionEvento(evento)) }
    .takeIf { esLaPrimeraPagina }

  AdelaidaText("${currentPage.inc()}/3")
  Row(horizontalArrangement = Arrangement.spacedBy(MargenEstandar, Alignment.CenterHorizontally)) {
    BotonPaginaAnterior(
      colorTexto, pagerState, cantidadPaginas, true, false,
      textoBotonAnterior, true, clicAnterior
    )
    BotonPaginaSiguiente(
      colorTexto, pagerState, cantidadPaginas, true, false,
      textoBotonSiguiente, cantidadPaginas == 3, clicSiguiente
    )
  }
}

@Composable
private fun BotonDesempate(onMensaje: (Mensaje) -> Unit, modifier: Modifier = Modifier) {
  AdelaidaDialogOutlinedButton({
    onMensaje(
      Mensaje(
        "Mientras haya empate, los empatados tirarán el dado y ganará " +
            "aquel jugador que saque el número más alto. Si se permite más de un ganador, " +
            "por ejemplo, se permiten dos, y han empatado tres jugadores, tiran el dado " +
            "los tres. Si sacan un 5, un 5 y un 6 quien sacó el 6 pasa a ser uno de los " +
            "ganadores y los otros dos tiene que seguir desempatando. Si sacan un 4, " +
            "un 5 y un 6, ganan quienes sacaron el 5 y el 6."
      )
    )
  }, modifier) {
    AdelaidaText("Cómo desempatar")
  }
}