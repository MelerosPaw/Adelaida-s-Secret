package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.dbo.PartidaDBO
import com.example.composetest.data.uc.UC.Respuesta
import javax.inject.Inject

class ObtenerPartidaUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<ObtenerPartidaUC.Parametros, Respuesta<PartidaDBO?>>() {

    class Parametros(val idPartida: Long) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<PartidaDBO?> {
        Logger.logSql("Obtener una partida")
        return baseDatos
            .partidaDao()
            .obtenerPartida(parametros.idPartida)
            ?.let { Respuesta.Valor(it.partida) }
            ?: Respuesta.Error("No se ha podido recuperar la partida")
    }
}