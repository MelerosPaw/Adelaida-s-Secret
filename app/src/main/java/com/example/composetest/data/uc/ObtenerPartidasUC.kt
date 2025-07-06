package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.mapper.fromdbo.toModel
import com.example.composetest.model.Partida
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObtenerPartidasUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<None, Flow<List<Partida>>>() {

    override suspend fun execute(parametros: None): Flow<List<Partida>> {
        Logger.logSql("Obtener un flow de todas las partidas para cargar una")
        return baseDatos.partidaDao().obtenerTodas()
            .map { lista ->
                lista
                    .mapNotNull { it.toModel() }
                    .sortedByDescending { it.fecha }
            }
    }
}