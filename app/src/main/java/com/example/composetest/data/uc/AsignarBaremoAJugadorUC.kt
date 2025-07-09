package com.example.composetest.data.uc

import com.example.composetest.data.uc.UC.Respuesta
import com.example.composetest.model.Jugador
import javax.inject.Inject

class AsignarBaremoAJugadorUC @Inject constructor(
    private val actualizarJugador: ActualizarJugadorUC,
): UC<AsignarBaremoAJugadorUC.Parametros, Respuesta<Boolean>>() {

    class Parametros(
        val jugador: Jugador,
        val idPartida: Long,
        val idBaremo: String
    ): UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
        actualizarJugador(
            ActualizarJugadorUC.Parametros(idPartida, jugador, "asignarle el baremo $idBaremo") {
                it.copy(baremo = idBaremo)
            }
        )
    }
}