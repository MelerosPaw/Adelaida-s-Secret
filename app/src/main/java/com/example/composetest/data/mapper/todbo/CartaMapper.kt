package com.example.composetest.data.mapper.todbo

import com.example.composetest.data.db.dbo.ElementoTableroDBO
import com.example.composetest.data.db.reduced.AsignacionElemento
import com.example.composetest.model.ElementoTablero.Carta

fun Carta.cambiarPoseedor(
    idJugador: Long?,
    idPartida: Long,
    idSecretoParaGuardar: String? // Hace falta tenerlo aquí para que no falle unas firmas de otro método llamado igual.
): AsignacionElemento {
    val id = ElementoTableroDBO.crearId(prefijo, valor)
    val monedas = (this as? Carta.Dinero)?.monedas
    return AsignacionElemento(prefijo.id, valor, id, idPartida, idJugador, monedas, null, false)
}