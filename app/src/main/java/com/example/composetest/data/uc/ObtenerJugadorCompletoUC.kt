package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.exception.AsignarDineroException
import com.example.composetest.data.db.relations.JugadorCompleto
import com.example.composetest.data.uc.UC.Respuesta
import com.example.composetest.model.Jugador

class ObtenerJugadorCompletoUC(
    private val baseDatos: AdelaidaDatabase,
) : UC<ObtenerJugadorCompletoUC.Parametros, Respuesta<JugadorCompleto>>() {

    class Parametros(val idPartida: Long, val jugador: Jugador): UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<JugadorCompleto> = with(parametros) {
        try {
            Logger.logSql("Obtener jugador completo")
            baseDatos.jugadorDao().obtener(idPartida, jugador.nombre)?.let {
                Respuesta.Valor(it)
            } ?: throw AsignarDineroException.JugadorNoObtenido(jugador.nombre, idPartida, null)
        } catch (e: Throwable) {
            crearError(e)
        }
    }
}