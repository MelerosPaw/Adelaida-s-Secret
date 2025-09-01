package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.reduced.PartidaConRonda
import com.example.composetest.data.mapper.fromdbo.toModel
import com.example.composetest.data.uc.PasarSiguienteRondaUC.Parametros
import com.example.composetest.data.uc.UC.Respuesta.Valor
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.navegacion.NavegadorRondas
import javax.inject.Inject

class PasarSiguienteRondaUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<Parametros, UC.Respuesta<Partida>>() {

    class Parametros(val partida: Partida): UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Partida> = with(parametros) {
        val nuevaRonda = NavegadorRondas.obtenerSiguienteRonda(partida.ronda)
        val actualizacion = when(nuevaRonda) {
            Partida.Ronda.MANANA -> PartidaConRonda(partida.id, nuevaRonda.id, partida.dia.inc(), partida.eventoActualEjecutado)
            Partida.Ronda.MEDIODIA, Partida.Ronda.TARDE -> PartidaConRonda(partida.id, nuevaRonda.id, partida.dia, partida.eventoActualEjecutado)
            Partida.Ronda.NOCHE -> PartidaConRonda(partida.id, nuevaRonda.id, partida.dia, false)
            Partida.Ronda.NO_VALIDA -> null
        }

        return if (actualizacion == null) {
            crearError(Exception("La ronda a la que se quiere cambiar es NO_VALIDA"))

        } else {
            try {
                Logger.logSql("Actualizar la ronda de una partida")
                baseDatos.partidaDao().actualizarRondaPartida(actualizacion) == 1
                baseDatos.partidaDao().obtenerPartidaOThrow(partida.id).toModel()
                    ?.let(::Valor)
                    ?: Respuesta.Error("No se ha podido obtener la partida tras haberla actualizado")
            } catch (e: Throwable) {
                crearError(e)
            }
        }
    }
}