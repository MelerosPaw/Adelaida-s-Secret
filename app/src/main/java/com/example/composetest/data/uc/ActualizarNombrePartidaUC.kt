package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import javax.inject.Inject

class ActualizarNombrePartidaUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<ActualizarNombrePartidaUC.Parametros, Boolean>() {

    class Parametros(val idPartida: Long, val nombreNuevo: String): UC.Parametros

    override suspend fun execute(parametros: Parametros): Boolean {
        Logger.logSql("Cambiarle el nombre a una partida")
        return baseDatos.partidaDao()
            .actualizarNombrePartida(parametros.idPartida, parametros.nombreNuevo) == 1
    }
}