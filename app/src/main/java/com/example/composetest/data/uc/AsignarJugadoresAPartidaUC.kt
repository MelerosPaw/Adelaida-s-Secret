package com.example.composetest.data.uc

import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.dbo.JugadorDBO
import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.data.uc.UC.Respuesta
import com.example.composetest.model.Jugador
import javax.inject.Inject

class AsignarJugadoresAPartidaUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
): UC<AsignarJugadoresAPartidaUC.Parametros, Respuesta<Unit>>() {

    class Parametros(
        val nombreJugadores: List<String>,
        val idPartida: Long
    ): UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Unit> = with(parametros) {
        nombreJugadores
            .map(::Jugador)
            .toTypedArray()
            .let { jugadores ->
                try {
                    val jugadoresDbo = jugadores.map { JugadorDBO(0L, it.nombre, idPartida) }
                    baseDatos.partidaDao().asignarJugadores(jugadoresDbo, idPartida)
                    Respuesta.Valor(Unit)
                } catch (e: BaseDatosException) {
                    crearError(e)
                }
            }
    }
}