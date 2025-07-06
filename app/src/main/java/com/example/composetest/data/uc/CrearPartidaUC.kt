package com.example.composetest.data.uc

import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.dbo.PartidaDBO
import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.extensions.formatearFecha
import com.example.composetest.model.Partida
import com.example.composetest.ui.compose.navegacion.NavegadorCreacion
import com.example.composetest.ui.compose.navegacion.NavegadorRondas
import javax.inject.Inject

class CrearPartidaUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
): UC<CrearPartidaUC.Parametros, UC.Respuesta<Partida>>() {

    class Parametros(val nombre: String): UC.Parametros
    override suspend fun execute(parametros: Parametros): Respuesta<Partida> = with (parametros) {
        try {
            val partida = Partida(
                id = 0L,
                nombre = nombre,
                estadoCreacion = NavegadorCreacion.PRIMER_ESTADO,
                ronda = NavegadorRondas.PRIMERA_RONDA
            )
            val partidaDbo = PartidaDBO(
                partida.id,
                partida.nombre,
                idEstado = partida.estadoCreacion.id,
                fecha = formatearFecha(partida.fecha),
                idRonda = partida.ronda.id,
                dia = partida.dia,
                fuerzaDefensa = partida.fuerzaDefensa
            )
            val idPartidaCreada = baseDatos.partidaDao().crearPartida(partidaDbo)
            Respuesta.Valor(partida.copy(id = idPartidaCreada))
        } catch (e: BaseDatosException) {
            crearError(e)
        }
    }
}