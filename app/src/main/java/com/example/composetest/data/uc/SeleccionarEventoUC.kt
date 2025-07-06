package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.dbo.EventoDBO
import com.example.composetest.data.uc.UC.Respuesta
import com.example.composetest.data.uc.UC.Respuesta.Valor
import com.example.composetest.model.Evento
import javax.inject.Inject

class SeleccionarEventoUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
): UC<SeleccionarEventoUC.Parametros, Respuesta<Boolean>>() {

    class Parametros(
        val idPartida: Long,
        val evento: Evento,
    ): UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
        val existe = baseDatos.partidaDao().cuantas(idPartida) == 1
        val eventoDBO = baseDatos.eventoDao().obtenerPorNombre(evento.nombre)
        
        if (existe && eventoDBO != null) {
            val asignado = actualizarEventoActual(idPartida, eventoDBO)
            Valor(asignado)
        } else {
            Valor(false)
        }
    }

    private fun actualizarEventoActual(idPartida: Long, eventoDBO: EventoDBO): Boolean {
        Logger.logSql("Actualizando el evento actual de una partida")
        return baseDatos.partidaDao().cambiarEventoActual(idPartida, eventoDBO.id, false) == 1
    }
}