package com.example.composetest.data.uc

import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.model.ElementoTablero.Pista
import com.example.composetest.model.Jugador
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Provider

class ReemplazarPistaDeVitrinaUC @Inject constructor(
    private val asignarElemento: Provider<AsignarElementoUC>,
) : UC<ReemplazarPistaDeVitrinaUC.Parametros, UC.Respuesta<Boolean>>() {

    class Parametros(
        val pistaAReubicar: Pista,
        val pistaADevolverAlTablero : Pista,
        val poseedor: Jugador,
        val idPartida: Long
    ) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
        try {
            coroutineScope {
                async { asignarPistaAJugador() }
                async { devolverPistaATablero() }
                Respuesta.Valor(true)
            }
        } catch (e: BaseDatosException) {
            crearError(e)
        }
    }

    private suspend fun Parametros.asignarPistaAJugador(): Respuesta<Boolean> =
        reasignar(pistaAReubicar, poseedor.nombre)

    private suspend fun Parametros.devolverPistaATablero() {
        reasignar(pistaADevolverAlTablero, null)
    }

    private suspend fun Parametros.reasignar(pista: Pista, nombre: String?): Respuesta<Boolean> =
        asignarElemento.get().invoke(
            AsignarElementoUC.Parametros(pista, nombre, idPartida, false, true)
        )
}