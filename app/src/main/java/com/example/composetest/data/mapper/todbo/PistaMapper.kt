package com.example.composetest.data.mapper.todbo

import com.example.composetest.data.db.dbo.ElementoTableroDBO
import com.example.composetest.data.db.reduced.AsignacionElemento
import com.example.composetest.model.ElementoTablero.Pista

fun Pista.cambiarPoseedor(
    idJugador: Long?,
    idPartida: Long,
    idSecretoParaGuardar: String?,
    esDesasignacion: Boolean
): AsignacionElemento {
    val id = ElementoTableroDBO.crearId(prefijo, valor)
    return AsignacionElemento(prefijo.id, valor, id, idPartida, idJugador, monedas,
        idSecretoParaGuardar, esDesasignacion)
}