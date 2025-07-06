package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.reduced.PartidaConEstado
import com.example.composetest.data.mapper.fromdbo.toModel
import com.example.composetest.data.uc.ActualizarEstadoCreacion.Parametros.ConId
import com.example.composetest.data.uc.ActualizarEstadoCreacion.Parametros.ConPartida
import com.example.composetest.data.uc.UC.Respuesta.Valor
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.navegacion.NavegadorCreacion
import javax.inject.Inject

class ActualizarEstadoCreacion @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<ActualizarEstadoCreacion.Parametros, UC.Respuesta<Partida>>() {

    sealed class Parametros() : UC.Parametros {
        class ConId(val idPartida: Long) : Parametros()
        class ConPartida(val partida: Partida): Parametros()
    }

    override suspend fun execute(parametros: Parametros): Respuesta<Partida> = with(parametros) {
        when (parametros) {
            is ConId -> parametros.obtenerPartidaYActualizar()
            is ConPartida -> parametros.actualizarEstado()
        }
    }

    private suspend fun ConId.obtenerPartidaYActualizar(): Respuesta<Partida> =
        baseDatos.partidaDao().obtenerPartidaOThrow(idPartida).toModel()
            ?.let { ConPartida(it).actualizarEstado() }
            ?: Respuesta.Error("No se ha podido obtener la partida para actualizarla")

    private suspend fun ConPartida.actualizarEstado(): Respuesta<Partida> {
        val idSiguienteEstado = NavegadorCreacion.obtenerSiguienteEstado(partida.estadoCreacion).id
        val actualizacion = PartidaConEstado(partida.id, idSiguienteEstado)

        return try {
            Logger.logSql("Actualizar el estado de creación de una partida")
            baseDatos.tableroDao().actualizarEstadoPartida(actualizacion) == 1
            baseDatos.partidaDao().obtenerPartidaOThrow(partida.id).toModel()
                ?.let(::Valor)
                ?: Respuesta.Error("No se ha podido obtener la partida tras haberla actualizado")
        } catch (e: Throwable) {
            crearError(e)
        }
    }
}