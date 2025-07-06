package com.example.composetest.ui.contracts

import com.example.composetest.model.Baremo
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.widget.EventoVO
import com.example.composetest.ui.viewmodel.NocheViewModel
import com.example.composetest.ui.viewmodel.NocheViewModel.EventoRealizandose

sealed class Intencion {
  class AbrirDialogoBaremos(val jugador: Jugador): Intencion()
  class CerrarBaremo(): Intencion()
  class AbrirDialogoSeleccionManualEventos(): Intencion()
  class CerrarDialogoSeleccionManualEventos(val ultimoEventoVisualizado: EventoVO): Intencion()
  class MostrarMensaje(val mensaje: Mensaje): Intencion()
  class SeleccionarEvento(val evento: EventoVO): Intencion()
  class RealizarEventoSeleccionado(val evento: EventoVO): Intencion()
  class CerrarRealizacionEvento(val evento: EventoRealizandose): Intencion()
  class DarEventoPorRealizado(val evento: EventoRealizandose): Intencion()
  class SeleccionarEventoAleatorio(): Intencion()
  class GuardarBaremo(val jugador: Jugador, val baremo: Baremo): Intencion()
  class OcultarDialogoEjecucionEvento(): Intencion()
  class MarcarGanadorEvento(val seleccionado: Boolean, val jugador: NocheViewModel.JugadorVO): Intencion()
}

fun interface Consumidor {

  fun consumir(vararg intenciones: Intencion)

  object Dummy: Consumidor {
    override fun consumir(vararg intenciones: Intencion) {
      // Función dummy para previews
    }
  }
}