package com.example.composetest.data.mapper.fromdbo

import com.example.composetest.Logger
import com.example.composetest.data.db.dbo.ElementoCartaDBO
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.ElementoTablero.Carta
import com.example.composetest.model.ElementoTablero.Carta.AcusacionExtra
import com.example.composetest.model.ElementoTablero.Carta.Brandy
import com.example.composetest.model.ElementoTablero.Carta.Dinero
import com.example.composetest.model.ElementoTablero.Carta.Llave
import com.example.composetest.model.ElementoTablero.Carta.Perseskud
import com.example.composetest.model.ElementoTablero.Carta.Prefijo
import com.example.composetest.model.ElementoTablero.Carta.Reputacion

fun ElementoCartaDBO.toModel(): Carta? = when (Prefijo.fromId(prefijo)) {
    Prefijo.LLAVE -> recrearLlave()
    Prefijo.DINERO -> recrearMoneda()
    Prefijo.BRANDY -> recrearBrandy()
    Prefijo.PERSESKUD -> Perseskud()
    Prefijo.REPUTACION -> valor?.let(::Reputacion).orWarning("Reputación", valor)
    Prefijo.ACUSACION_EXTRA -> AcusacionExtra(gastado)
    null -> null
}

fun Carta.toDBO(idJugador: Long?, partida: Long): ElementoCartaDBO =
    ElementoCartaDBO(prefijo.id, valor, partida, (this as? Dinero)?.monedas, idJugador)

private fun ElementoCartaDBO.recrearLlave(): Llave? =
    noneNull(valor?.toIntOrNull(), gastado, ::Llave).orWarning("Llave", valor)

private fun ElementoCartaDBO.recrearMoneda(): Carta? =
    noneNull(valor?.toIntOrNull(), monedas) { valor, dinero ->
        Dinero(valor, dinero, gastado)
    } ?: run {
        Logger("ElementoCartaMapper")
            .log("No se ha podido recrear una carta Dinero con el valor $valor y la cantidad de " +
                    "$monedas monedas"
        )
        null
    }

private fun ElementoCartaDBO.recrearBrandy(): Brandy? =
    noneNull(valor?.toIntOrNull(), gastado, ::Brandy).orWarning("Brandy", valor)

private fun <T> T?.orWarning(tipo: String, valor: String?): T? {
    if (this == null) {
        Logger("ElementoCartaMapper").log("No se ha podido recrear una carta $tipo porque su " +
                "valor es $valor")
    }
    return this
}