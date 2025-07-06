package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.reduced.PartidaConRonda
import com.example.composetest.data.mapper.fromdbo.toModel
import com.example.composetest.data.uc.ActualizarRondaUC.Parametros
import com.example.composetest.data.uc.UC.Respuesta.Valor
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.navegacion.NavegadorRondas
import javax.inject.Inject

class ActualizarRondaUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<Parametros, UC.Respuesta<Partida>>() {

    class Parametros(val partida: Partida): UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Partida> = with(parametros) {
        val nuevaRonda = NavegadorRondas.obtenerSiguienteRonda(partida.ronda)
        val actualizacion = PartidaConRonda(partida.id, nuevaRonda.id)

        return try {
            Logger.logSql("Actualizar la ronda de una partida")
            baseDatos.tableroDao().actualizarRondaPartida(actualizacion) == 1
            baseDatos.partidaDao().obtenerPartidaOThrow(partida.id).toModel()
                ?.let(::Valor)
                ?: Respuesta.Error("No se ha podido obtener la partida tras haberla actualizado")
        } catch (e: Throwable) {
            crearError(e)
        }
    }
}