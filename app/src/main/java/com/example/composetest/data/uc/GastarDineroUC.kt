package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.mapper.todbo.toActualizacion
import com.example.composetest.model.Jugador
import javax.inject.Inject

class GastarDineroUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<GastarDineroUC.Parametros, UC.Respuesta<Boolean>>() {

    class Parametros(val cantidad: Int, val deQuien: Jugador, val idPartida: Long) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
        val dineroFinal = deQuien.dinero - cantidad

        return if (dineroFinal < 0) {
            Respuesta.Error("El jugador se quedaría con $dineroFinal solamente.")
        } else {
            val jugador = deQuien.toActualizacion(idPartida)

            val actualizados = try {
                baseDatos.asignarElementoDao().actualizarJugador(jugador) {
                    it.copy(dinero = dineroFinal)
                }
            } catch (t: Throwable) {
                logger.error("No se ha podido restar $cantidad monedas a las ${deQuien.dinero}) " +
                    "monedas que tiene ${deQuien.nombre}", t)
            }

            if (actualizados != 1) {
                Respuesta.Error("Se han actualizado $actualizados jugadores")
            } else {
                Respuesta.Valor(true)
            }
        }
    }
}