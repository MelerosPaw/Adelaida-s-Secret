package com.example.composetest.data.uc

import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.data.mapper.fromdbo.getIdSecretoParaGuardarSiAplica
import com.example.composetest.data.mapper.todbo.cambiarPoseedor
import com.example.composetest.data.mapper.todbo.toActualizacion
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import javax.inject.Inject

class ComprarUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<ComprarUC.Parametros, UC.Respuesta<Boolean>>() {

    class Parametros(
        val pista: ElementoTablero.Pista,
        val cantidad: Int,
        val comprador: Jugador,
        val idPartida: Long
    ) : UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
        val compradorDBO = baseDatos.asignarElementoDao().buscarJugador(comprador.nombre, idPartida)
        val idSecretoParaGuardar = compradorDBO.getIdSecretoParaGuardarSiAplica(pista)
        val pistaActualizada = pista.cambiarPoseedor(compradorDBO.id, idPartida, idSecretoParaGuardar)
        val jugadorActualizado = comprador.toActualizacion(idPartida, comprador.dinero - cantidad)

        return try {
            baseDatos.asignarElementoDao().comprar(pistaActualizada, jugadorActualizado)
            Respuesta.Valor(true)
        } catch (e: BaseDatosException) {
            crearError(e)
        }
    }
}