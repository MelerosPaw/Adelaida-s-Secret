package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.mapper.fromdbo.toModel
import com.example.composetest.model.Partida
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObtenerPartidaFlowUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<ObtenerPartidaFlowUC.Parametros, Flow<UC.Respuesta<Partida?>>>() {

    class Parametros(val idPartida: Long) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Flow<Respuesta<Partida?>> {
        Logger.logSql("Obtener un flow de la partida")
        return baseDatos.partidaDao()
            .obtener(parametros.idPartida)
            .map { partidaCompleta ->
                partidaCompleta.toModel()
                    ?.let { Respuesta.Valor(it) }
                    ?: Respuesta.Error("No se ha podido recuperar la partida")
            }
    }
}