package com.example.composetest.ui.contracts

import com.example.composetest.model.Baremo
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.navegacion.Mensaje
import com.example.composetest.ui.compose.widget.EventoVO
import com.example.composetest.ui.viewmodel.NocheViewModel
import com.example.composetest.ui.viewmodel.NocheViewModel.EventoRealizandose

sealed class IntencionNoche {
  class AbrirDialogoBaremos(val jugador: Jugador): IntencionNoche()
  class CerrarBaremo(): IntencionNoche()
  class AbrirDialogoSeleccionManualEventos(): IntencionNoche()
  class CerrarDialogoSeleccionManualEventos(val ultimoEventoVisualizado: EventoVO): IntencionNoche()
  class MostrarMensaje(val mensaje: Mensaje): IntencionNoche()
  class SeleccionarEvento(val evento: EventoVO): IntencionNoche()
  class RealizarEventoSeleccionado(val evento: EventoVO): IntencionNoche()
  class CerrarRealizacionEvento(val evento: EventoRealizandose): IntencionNoche()
  class DarEventoPorRealizado(val evento: EventoRealizandose): IntencionNoche()
  class SeleccionarEventoAleatorio(): IntencionNoche()
  class GuardarBaremo(val jugador: Jugador, val baremo: Baremo): IntencionNoche()
  class OcultarDialogoEjecucionEvento(): IntencionNoche()
  class MarcarGanadorEvento(val seleccionado: Boolean, val jugador: NocheViewModel.JugadorVO): IntencionNoche()
  class Visitar(val jugador: Jugador): IntencionNoche()
}

fun interface ConsumidorNoche: Consumidor<IntencionNoche> {

}

fun interface Consumidor<INTENT> {

  fun consumir(vararg intenciones: INTENT)

  object Dummy: ConsumidorNoche {
    override fun consumir(vararg intenciones: IntencionNoche) {
      // Función dummy para previews
    }
  }
}