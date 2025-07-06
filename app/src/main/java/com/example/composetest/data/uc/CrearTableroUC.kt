package com.example.composetest.data.uc

import com.example.composetest.data.uc.UC.Respuesta
import com.example.composetest.model.Partida
import com.example.composetest.model.Tablero
import com.example.composetest.model.elementosJuego
import javax.inject.Inject

class CrearTableroUC @Inject constructor(): UC<CrearTableroUC.Parametros, Respuesta<Tablero>>() {

    class Parametros(val partida: Partida?): UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Tablero> = with(parametros) {
        val idPistasAsesino = partida?.asesino?.let { asesino ->
            asesino.rasgos.map { it.idPista } +
                    asesino.secreto.idPista +
                    asesino.secreto.idSecretoVinculado
        } ?: emptyList()

        val elementos = elementosJuego.filterNot {
            it.id in idPistasAsesino
        }.take(64)

        // Esto se pone para que el último secreto, que tiene que caer a la fuerza en una habitación
        // donde ya hay otro (porque hay 9 secretos y 8 habitaciones), no tenga problemas en
        // compartir habitación con otro.
        elementos[8].cantidadPermitidaPorHabitacion = null

        when {
            partida != null && partida.asesino?.secreto?.idSecretoVinculado == null ->
                Respuesta.Error("El asesino de la partida (${partida.asesino?.nombre}) no tiene secreto asociado: ${partida.asesino?.descripcionRasgos()}")
            elementos.size != 64 ->
                Respuesta.Error("Hay ${elementos.size} elementos, de los cuales se han sustraido ${idPistasAsesino.size}")
            else ->
                Respuesta.Valor(Tablero.Constructor(elementos = elementos.toTypedArray()).construir())
        }
    }
}