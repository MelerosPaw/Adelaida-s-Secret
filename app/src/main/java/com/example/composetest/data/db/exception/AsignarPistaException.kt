package com.example.composetest.data.db.exception

import com.example.composetest.data.db.reduced.AsignacionElemento

sealed class AsignarPistaException(
    mensaje: String,
    cause: Throwable?
) : BaseDatosException(mensaje, cause) {

    class PistaNoCambiadaDePoseedor(pista: AsignacionElemento, cantidadActualizados: Int?) :
        AsignarPistaException("No se ha podido asignar la pista al jugador:\n$pista" +
                "Se han modificado $cantidadActualizados registros.", null
        )
}