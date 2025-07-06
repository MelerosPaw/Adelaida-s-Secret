package com.example.composetest.data.uc

import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.model.Partida
import javax.inject.Inject

class BorrarPartidaCompletaUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<BorrarPartidaCompletaUC.Parametros, UC.Respuesta<Boolean>>() {

    class Parametros(val partida: Partida) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
        try {
            baseDatos.partidaDao().borrar(partida.id)
            Respuesta.Valor(true)
        } catch (e: BaseDatosException) {
            crearError(e)
        }
    }
}