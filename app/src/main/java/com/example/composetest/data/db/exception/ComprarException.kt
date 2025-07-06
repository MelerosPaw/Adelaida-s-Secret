package com.example.composetest.data.db.exception

import com.example.composetest.data.db.reduced.ActualizacionJugador

sealed class ComprarException(
    mensaje: String,
    causa: Throwable? = null
) : BaseDatosException(mensaje, causa) {

    class DineroNegativo(comprador: ActualizacionJugador): ComprarException(
        "${comprador.nombre} se quedaría con ${comprador.dinero} monedas. Eso no puede ser."
    )

    class CompradorNoActualizado(comprador: ActualizacionJugador, t: Throwable): ComprarException(
        "No se han podido asignar ${comprador.dinero} monedas a ${comprador.nombre}.", t
    )

    class CompradoresActualizadosIncorrectos(cantidadActualizados: Int) : ComprarException(
        "Se han actualizado $cantidadActualizados jugadores en lugar de solo uno."
    )
}