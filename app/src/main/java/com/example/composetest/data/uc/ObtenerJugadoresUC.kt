package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.mapper.fromdbo.toModel
import com.example.composetest.model.Jugador
import javax.inject.Inject

class ObtenerJugadoresUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
): UC<ObtenerJugadoresUC.Parametros, List<Jugador>>() {

    class Parametros(val nombrePartida: String): UC.Parametros

    override suspend fun execute(parametros: Parametros): List<Jugador> {
        Logger.logSql("Obtener los de una partida por el nombre de la partida")
        return baseDatos.partidaDao().obtenerPorNombre(parametros.nombrePartida)
            ?.jugadores?.map { info -> info.toModel() }.orEmpty()
    }
}