package com.example.composetest.data.uc

import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.model.Partida
import javax.inject.Inject

class AsignarJugadoresYNombrePartida @Inject constructor(
    private val asignarJugadores: AsignarJugadoresAPartidaUC,
    private val actualizarNombrePartida: ActualizarNombrePartidaUC,
    private val actualizarEstadoCreacion: ActualizarEstadoCreacion
): UC<AsignarJugadoresYNombrePartida.Parametros, UC.Respuesta<Partida>>() {

    class Parametros(
        val nombreJugadores: List<String>,
        val nombrePartida: String,
        val idPartida: Long,
    ): UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Partida> = with(parametros) {
        try {
            asignarJugadores(AsignarJugadoresAPartidaUC.Parametros(nombreJugadores, idPartida))
            actualizarNombrePartida(ActualizarNombrePartidaUC.Parametros(idPartida, nombrePartida))
            actualizarEstadoCreacion(ActualizarEstadoCreacion.Parametros.ConId(idPartida))
        } catch (e: BaseDatosException) {
            crearError(e)
        }
    }
}