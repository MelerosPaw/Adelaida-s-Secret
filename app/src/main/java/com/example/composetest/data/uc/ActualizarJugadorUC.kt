package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.dbo.JugadorDBO
import com.example.composetest.data.uc.ActualizarJugadorUC.Parametros
import com.example.composetest.model.Jugador
import javax.inject.Inject

class ActualizarJugadorUC @Inject constructor(
  private val baseDatos: AdelaidaDatabase,
) : UC<Parametros, UC.Respuesta<Boolean>>() {

  class Parametros(
    val idPartida: Long,
    val jugador: Jugador,
    val explicacionActualizacion: String,
    val actualizacion: (jugadorDeBaseDatos: JugadorDBO) -> JugadorDBO,
  ) : UC.Parametros

  override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
    Logger.logSql("Obtener un jugador")
    val jugador = baseDatos.jugadorDao().obtener(idPartida, jugador.nombre)?.jugador

    return if (jugador == null) {
      Respuesta.Error("No se ha podido obtener el jugador ${this.jugador.nombre}")
    } else {
      Logger.logSql("Actualizar un jugador")
      val actualizados = actualizacion(jugador).let(baseDatos.jugadorDao()::actualizar)

      if (actualizados == 1) {
        Respuesta.Valor(true)
      } else {
        Respuesta.Error("No se ha podido actualiar el jugador ${jugador.nombre} para $explicacionActualizacion")
      }
    }
  }
}