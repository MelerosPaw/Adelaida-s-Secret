package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.relations.JugadorCompleto
import com.example.composetest.data.mapper.fromdbo.toModel
import com.example.composetest.model.Jugador
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObtenerJugadoresDePartidaUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
): UC<ObtenerJugadoresDePartidaUC.Parametros, Flow<List<Jugador>>>() {

    class Parametros(val partida: Long): UC.Parametros

    override suspend fun execute(parametros: Parametros): Flow<List<Jugador>> {
        Logger.logSql("Obtener jugadores de una partida")
        return baseDatos.jugadorDao()
            .obtenerPorPartida(parametros.partida)
            .map { lista -> lista.map(JugadorCompleto::toModel) }
    }
}