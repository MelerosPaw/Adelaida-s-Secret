package com.example.composetest.data.uc

import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.data.db.reduced.ActualizacionJugador
import com.example.composetest.data.mapper.fromdbo.getIdSecretoParaGuardarSiAplica
import com.example.composetest.data.mapper.fromdbo.toDBO
import com.example.composetest.data.mapper.todbo.cambiarPoseedor
import com.example.composetest.data.mapper.todbo.toActualizacion
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.ElementoTablero.Carta
import com.example.composetest.model.ElementoTablero.Carta.Dinero
import com.example.composetest.model.ElementoTablero.Pista
import com.example.composetest.model.Jugador
import javax.inject.Inject

class RobarUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<RobarUC.Parametros, UC.Respuesta<Boolean>>() {

    class Parametros(
        val brandy: Carta.Brandy,
        val ladron: Jugador,
        val victima: Jugador,
        val cosaRobada: ElementoTablero,
        val idPartida: Long
    ) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
        val ladronDBO = baseDatos.asignarElementoDao().buscarJugador(ladron.nombre, idPartida)
        val brandyGastado = brandy.toDBO(ladronDBO.id, idPartida)
        val idSecretoParaGuardar = (cosaRobada as? Pista)?.let(ladronDBO::getIdSecretoParaGuardarSiAplica)
        val elementoRobado = cosaRobada.cambiarPoseedor(ladronDBO.id, idPartida, idSecretoParaGuardar)
        val victimaActualizada = victimaSinElDinero()

        return try {
            with(baseDatos.asignarElementoDao()) {
                when (cosaRobada) {
                    is Carta -> robarCarta(brandyGastado, elementoRobado, ladronDBO, victimaActualizada, idPartida)
                    is Pista -> robarPista(brandyGastado, elementoRobado, idPartida)
                }
            }
            Respuesta.Valor(true)
        } catch (e: BaseDatosException) {
            crearError(e)
        }
    }

    private fun Parametros.victimaSinElDinero(): ActualizacionJugador {
        val dinero = victima.dinero - ((cosaRobada as? Dinero)?.monedas ?: 0)
        return victima.toActualizacion(idPartida, dinero)
    }
}