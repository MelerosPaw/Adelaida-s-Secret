package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.mapper.fromdbo.toModel
import com.example.composetest.data.uc.UC.Respuesta
import com.example.composetest.model.Evento
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ObtenerEventosUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
): UC<ObtenerEventosUC.Parametros, Respuesta<List<Evento>>>() {

    class Parametros(): UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<List<Evento>> = with(parametros) {
        try {
            Logger.logSql("Obtener todos los eventos")
            val eventosDbo = baseDatos.eventoDao().obtenerTodos()
            val eventos: List<Evento> = coroutineScope {
                eventosDbo
                    .map { evento -> async { evento.toModel() } }
                    .awaitAll()
                    .filterNotNull()
            }
            Respuesta.Valor(eventos)
        } catch (t: Throwable) {
            crearError(t)
        }
    }
}