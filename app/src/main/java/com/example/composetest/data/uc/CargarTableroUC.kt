package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.mapper.fromdbo.toModel
import com.example.composetest.model.Partida
import com.example.composetest.model.Tablero
import javax.inject.Inject

class CargarTableroUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<CargarTableroUC.Parametros, UC.Respuesta<CargarTableroUC.PartidaYTablero>>() {

    class Parametros(val idPartida: Long) : UC.Parametros
    class PartidaYTablero(val partida: Partida, val tablero: Tablero?) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<PartidaYTablero> = with(parametros) {
        Logger.logSql("Obtener una partida para cargar su tablero")
        baseDatos.partidaDao().obtenerPartida(idPartida)
            ?.let {
                val partida = it.toModel()
                val tablero = it.tablero?.toModel()

                partida?.let {
                    Respuesta.Valor(PartidaYTablero(partida, tablero))
                }
            } ?: Respuesta.Error("No se ha podido cargar la partida con el id $idPartida")
    }
}