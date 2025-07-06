package com.example.composetest.ui.compose.screen

import BotonDialogoDefaults
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composetest.R
import com.example.composetest.extensions.esElMismoDia
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.Cargando
import com.example.composetest.ui.compose.navegacion.CLAVE_CARGA_INICIAL
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.navegacion.NavegadorCreacion
import com.example.composetest.ui.compose.sampledata.partidas
import com.example.composetest.ui.compose.theme.ListItemSubtitleIndentation
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.AdelaidaDivider
import com.example.composetest.ui.compose.widget.AdelaidaIconButton
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.viewmodel.CargarPartidaViewModel

@Composable
fun ScreenCargarPartida(
  onConfiguracionToolbarCambiada: (NavegadorCreacion.ConfiguracionToolbar) -> Unit,
  onNavegarASiguientePantalla: (partida: Partida) -> Unit,
  onNavegarAtras: () -> Unit,
  onMensaje: (Mensaje) -> Unit
) {
  val viewModel: CargarPartidaViewModel = hiltViewModel()
  viewModel.onMensaje = onMensaje

  LaunchedEffect(CLAVE_CARGA_INICIAL) {
    viewModel.cargarPartidas()
  }

  ScreenCargarPartidas(viewModel, onNavegarAtras, onNavegarASiguientePantalla)
}

@Composable
private fun ScreenCargarPartidas(
  viewModel: CargarPartidaViewModel,
  onNavegarAtras: () -> Unit,
  onNavegarASiguientePantalla: (Partida) -> Unit
) {
  Screen(
    configuracionToolbar = NavegadorCreacion.ConfiguracionToolbar(
      titulo = NavegadorCreacion.ConfiguracionToolbar.titulo("Cargar partida"),
      iconoNavegacion = {
        AdelaidaIconButton(
          Icons.AutoMirrored.Default.ArrowBack,
          "Volver",
          onClick = onNavegarAtras,
          tint = Tema.colors.toolbarContent
        )
      },
    )
  ) {
    Box(Modifier.padding(MargenEstandar)) {
      Partidas(viewModel)
    }

    DialogoCargarOEliminar(viewModel)
    DialogoConfirmar(viewModel, onNavegarASiguientePantalla)
  }
}

@Composable
fun Partidas(viewModel: CargarPartidaViewModel) {
  val partidas by remember { viewModel.listadoPartidas }
  val estadoFlow = partidas?.collectAsStateWithLifecycle(null)
  ListadoPartidas(estadoFlow?.value) { partida -> viewModel.mostrarCargarEliminar(partida) }
}

@Composable
private fun ListadoPartidas(partidas: List<Partida>?, onPartidaClicada: (Partida) -> Unit) {
  when {
    partidas == null -> CargandoPartidas()
    partidas.isEmpty() -> SinPartidas()
    else -> ListaPartidas(partidas, onPartidaClicada)
  }
}

@Composable
private fun CargandoPartidas() {
  Box(Modifier.fillMaxSize(), propagateMinConstraints = true) {
    Cargando()
  }
}

@Composable
private fun SinPartidas() {
  Box(Modifier.fillMaxSize()) {
    AdelaidaText("<Sin partidas>", modifier = Modifier.align(Alignment.TopCenter))
  }
}

@Composable
private fun ListaPartidas(partidas: List<Partida>, onPartidaClicada: (Partida) -> Unit) {
  LazyColumn(Modifier
    .fillMaxSize()
    .background(Tema.colors.fondoListas)) {
    itemsIndexed(partidas, { i, p -> p.id }, { i, p -> p }) { index, partida ->
      val separador = when {
        esLaUltima(index, partidas) -> Separador.SIN
        esEnElMismoDiaQueLaSiguiente(partidas, index, partida) -> Separador.SIMPLE
        else -> Separador.DOBLE
      }
      PartidaHolder(partida, separador, onPartidaClicada)
    }
  }
}

@Composable
private fun PartidaHolder(partida: Partida, separador: Separador, onPartidaClicada: (Partida) -> Unit) {
  Column(Modifier.padding(top = 10.dp)) {
    Row(
      Modifier
        .fillMaxWidth()
        .clickable { onPartidaClicada(partida) }
        .padding(horizontal = MargenEstandar)
    ) {
      Image(
        painterResource(R.drawable.bullet_point),
        null,
        Modifier
          .padding(top = 6.dp)
          .height(BotonDialogoDefaults.BULLET_POINT_SIZE.dp),
        colorFilter = ColorFilter.tint(Tema.colors.texto),
      )
      Column(Modifier.padding(start = 8.dp)) {
        Titulo(partida.nombre, nivel = NivelTitulo.Nivel2)
        AdelaidaText(partida.info(), Modifier.padding(bottom = 10.dp))
      }
    }

    when(separador) {
      Separador.SIN -> {}
      Separador.SIMPLE -> AdelaidaDivider(Modifier.padding(horizontal = ListItemSubtitleIndentation))
      Separador.DOBLE -> {
        AdelaidaDivider(
          Modifier
            .padding(horizontal = ListItemSubtitleIndentation)
            .padding(bottom = 2.dp))
        AdelaidaDivider(Modifier.padding(horizontal = ListItemSubtitleIndentation))
      }
    }
  }
}

@Composable
private fun DialogoCargarOEliminar(viewModel: CargarPartidaViewModel) {
  val mostrarDialogoCargarEliminar by remember { viewModel.mostrarCargarEliminar }

  mostrarDialogoCargarEliminar.partidaClicada?.let {
    AdelaidaButtonDialog(it.nombre,
      arrayOf(
        OpcionDialogo("Cargar", it, true, viewModel::mostrarConfirmacionCarga),
        OpcionDialogo("Borrar", it, true, viewModel::mostrarConfirmacionBorrar),
        OpcionDialogo("Nada", it) { viewModel.ocultarCargarEliminar() },
      )
    )
  }
}

@Composable
private fun DialogoConfirmar(
  viewModel: CargarPartidaViewModel,
  onNavegarASiguientePantalla: (partida: Partida) -> Unit
) {
  val mostrarDialogoCargarEliminar by remember { viewModel.mostrarCargarEliminar }

  noneNull(
    mostrarDialogoCargarEliminar.partidaClicada,
    mostrarDialogoCargarEliminar.finalidad
  ) { partida, finalidad ->
    val mensaje = when(finalidad) {
      CargarPartidaViewModel.EstadoCargarEliminar.Finalidad.BORRAR -> "¿Seguro que quieres borrar ${partida.nombre}?"
      CargarPartidaViewModel.EstadoCargarEliminar.Finalidad.CARGAR -> "¿Cargar ${partida.nombre}?"
    }

    AdelaidaButtonDialog(mensaje, arrayOf(
      OpcionDialogo<Partida>("Sí", partida, true) {
        viewModel.confirmar(it, finalidad, onNavegarASiguientePantalla)
      },
      OpcionDialogo<Partida>("No", partida, true, viewModel::ocultarConfirmacion)
    )
    )
  }
}

@Composable
private fun esLaUltima(posicion: Int, partidas: List<Partida>): Boolean =
  posicion == partidas.size - 1

@Composable
private fun esEnElMismoDiaQueLaSiguiente(
  partidas: List<Partida>,
  index: Int,
  partida: Partida
): Boolean = partidas.getOrNull(index.inc())?.fecha?.let { it esElMismoDia partida.fecha } == true

private enum class Separador {
  SIN, SIMPLE, DOBLE
}

@Composable
@Preview(name = "Claro", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ListaMarron() {
  ScreenPreviewMarron {
    Box(Modifier.fillMaxSize().padding(MargenEstandar)) {
      ListadoPartidas(partidas(40)) { }
    }
  }
}

@Composable
@Preview(name = "Claro", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ListaVerde() {
  ScreenPreviewVerde {
    Box(Modifier
      .fillMaxSize()
      .padding(MargenEstandar)) {
      ListadoPartidas(partidas(40)) { }
    }
  }
}
