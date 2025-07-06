package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import javax.inject.Inject

class PartidaRepetidaUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<PartidaRepetidaUC.Parametros, Boolean>() {

    class Parametros(val nombrePartida: String): UC.Parametros

    override suspend fun execute(parametros: Parametros): Boolean {
        Logger.logSql("Buscar el nombre de una partida para ver si está repetido")
        return baseDatos.partidaDao().obtenerNombrePartida(parametros.nombrePartida) != null
    }
}