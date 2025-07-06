package com.example.composetest.data.db.exception

import com.example.composetest.data.db.dbo.JugadorDBO
import com.example.composetest.data.db.reduced.AsignacionElemento

sealed class AsignarDineroException(
    mensaje: String,
    cause: Throwable?
) : BaseDatosException(mensaje, cause) {

    class JugadorNoObtenido(nombre: String, idPartida: Long, cause: Throwable?) :
        AsignarDineroException(
            "No se ha podido obtener el jugador\n${nombre}, idPartida: $idPartida",
            cause
        )

    class DineroNoSumado(actualizados: Int?, jugador: JugadorDBO, cause: Throwable?) :
        AsignarDineroException(actualizados?.let {
            "Al sumar el dinero, se han modificado $it registros\n$jugador"
        } ?: "No se ha podido sumar el dinero al jugador\n$jugador", cause)

    class CartaNoGastada(
        idCarta: String,
        idPartida: Long,
        cantidadBorrados: Int?,
        causa: Throwable?
    ) : AsignarDineroException(
        "No se ha podido eliminar el dinero: " +
                "id: $idCarta, idPartida: $idPartida\n" +
                "Se han borrado $cantidadBorrados elementos\n", causa
    )

    class CartaNoCambiadaDePoseedor(carta: AsignacionElemento, cantidadActualizados: Int?) :
        AsignarDineroException(
            "No se ha podido asignar la carta al jugador:\n$carta" +
                    "Se han modificado $cantidadActualizados registros.", null
        )
}