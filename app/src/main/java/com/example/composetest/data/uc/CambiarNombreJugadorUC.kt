package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.model.Jugador
import javax.inject.Inject

class CambiarNombreJugadorUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<CambiarNombreJugadorUC.Parametros, Boolean>() {

    class Parametros(val jugador: Jugador, val idPartida: Long, val nombre: String) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Boolean = with(parametros) {
        Logger.logSql("Cambiar nombre de jugador")
        baseDatos.jugadorDao().cambiarNombre(nombre, jugador.nombre, idPartida)
        true
    }
}