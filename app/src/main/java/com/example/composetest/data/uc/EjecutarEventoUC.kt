package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.dbo.EventoDBO
import com.example.composetest.data.uc.UC.Respuesta
import com.example.composetest.data.uc.UC.Respuesta.Valor
import com.example.composetest.model.Evento
import com.example.composetest.model.Jugador
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class EjecutarEventoUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
    private val actualizarJugadorUC: ActualizarJugadorUC,
): UC<EjecutarEventoUC.Parametros, Respuesta<Boolean>>() {

    class Parametros(
        val idPartida: Long,
        val evento: Evento,
        val ganadores: List<Jugador>,
    ): UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
        val accion = evento.accion
        val existe = baseDatos.partidaDao().cuantas(idPartida) == 1

        if (existe) {
            val ejecutado = baseDatos.partidaDao().actualizarEventoEjecutado(idPartida, true) == 1

            val accionRealizada = if (ejecutado) {
                when (accion) {
                    is Evento.Accion.OtorgarComodin -> asignarComodin(idPartida, accion.comodin, ganadores)
                    is Evento.Accion.AplicarEfecto -> asignarEfecto(idPartida, accion.efecto)
                }
            } else {
                false
            }
            Valor(accionRealizada)
        } else {
            Valor(false)
        }
    }

    private suspend fun asignarComodin(
        idPartida: Long,
        comodin: Evento.Comodin,
        ganadores: List<Jugador>,
    ): Boolean = coroutineScope {
        ganadores.map {
            async {
                actualizarJugadorUC(
                    ActualizarJugadorUC.Parametros(
                        idPartida = idPartida,
                        jugador = it,
                        explicacionActualizacion = "asignarle el comodín ${comodin.nombre} (${comodin.id})",
                        actualizacion = { it.copy(comodines =
                            if (it.comodines == null) {
                                comodin.id
                            } else {
                                it.comodines + "|" + comodin.id
                            }
                        ) }
                    )
                ).let { it is Valor && it.valor }
            }
        }
            .awaitAll()
            .all { it }
    }

    private fun asignarEfecto(idPartida: Long, efecto: Evento.Efecto): Boolean {
        return false
    }
}