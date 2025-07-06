package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.reduced.PartidaYAsesino
import com.example.composetest.data.uc.UC.Respuesta
import com.example.composetest.model.Partida
import com.example.composetest.model.Sospechoso
import javax.inject.Inject

class AsignarAsesinoUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
    private val actualizarEstadoCreacion: ActualizarEstadoCreacion
) : UC<AsignarAsesinoUC.Parametros, Respuesta<Partida>>() {

    class Parametros(val idPartida: Long, val asesino: Sospechoso) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Partida> = with(parametros) {
        Logger.logSql("Obtener el id de un sospechoso por nombre")
        val idAsesino = baseDatos.sospechosoDao().obtenerId(asesino.nombre)

        if (idAsesino == null) {
            Respuesta.Error("No se ha encontrado el id del asesino (${asesino.nombre}")

        } else {
            val actualizacion = PartidaYAsesino(idPartida, idAsesino)
            Logger.logSql("Marcar un sospechoso como asesino de una partida")
            val actualizados = baseDatos.partidaDao().asignarAsesino(actualizacion)

            if (actualizados != 1) {
                Respuesta.Error("Al guardar al asesino se han modificado $actualizados registros.")

            } else {
                actualizarEstadoCreacion(ActualizarEstadoCreacion.Parametros.ConId(idPartida))
            }
        }
    }
}