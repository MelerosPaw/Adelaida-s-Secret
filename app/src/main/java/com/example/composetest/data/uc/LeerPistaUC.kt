package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.model.ContenidoPista
import javax.inject.Inject

class LeerPistaUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<LeerPistaUC.Parametros, UC.Respuesta<ContenidoPista>>() {

    class Parametros(val idPista: String) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<ContenidoPista> = with(parametros) {
        Logger.logSql("Obtener pista para leerla")
        baseDatos.contenidoPistaDao().obtener(idPista)
            ?.let { Respuesta.Valor(ContenidoPista(it.texto)) }
            ?: Respuesta.Error("No se ha podido cargar la pista")
    }
}