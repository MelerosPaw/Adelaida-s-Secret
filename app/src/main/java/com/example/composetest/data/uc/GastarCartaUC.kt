package com.example.composetest.data.uc

import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.data.mapper.fromdbo.toDBO
import com.example.composetest.model.ElementoTablero
import javax.inject.Inject

class GastarCartaUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<GastarCartaUC.Parametros, UC.Respuesta<Boolean>>() {

    class Parametros(val carta: ElementoTablero.Carta, val idPartida: Long) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
        try {
            val id = carta.toDBO(null, idPartida) .idElemento
            baseDatos.asignarElementoDao().gastar(id, idPartida)
            Respuesta.Valor(true)
        } catch (e: BaseDatosException) {
            crearError(e)
        }
    }
}