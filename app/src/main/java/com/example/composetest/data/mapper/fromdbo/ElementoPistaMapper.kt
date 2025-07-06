package com.example.composetest.data.mapper.fromdbo

import com.example.composetest.Logger
import com.example.composetest.data.db.dbo.ElementoPistaDBO
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.ElementoTablero.Pista
import com.example.composetest.model.ElementoTablero.Pista.Coartada
import com.example.composetest.model.ElementoTablero.Pista.Habito
import com.example.composetest.model.ElementoTablero.Pista.Objeto
import com.example.composetest.model.ElementoTablero.Pista.Prefijo
import com.example.composetest.model.ElementoTablero.Pista.Secreto
import com.example.composetest.model.ElementoTablero.Pista.Testigo

fun ElementoPistaDBO.toModel(): Pista? = when (val prefijo = Prefijo.fromId(prefijo)) {
    null -> null
    Prefijo.HABITO -> toPista(prefijo, ::Habito)
    Prefijo.OBJETO -> toPista(prefijo, ::Objeto)
    Prefijo.TESTIGO -> toPista(prefijo, ::Testigo)
    Prefijo.COARTADA -> toPista(prefijo, ::Coartada)
    Prefijo.SECRETO -> toPista(prefijo, ::Secreto)
}

private fun ElementoPistaDBO.toPista(prefijo: Prefijo, convertir: (Int, Int) -> Pista?): Pista? =
    monedas?.let { toPistaFalsa(prefijo, it) }
        ?: noneNull(valor?.toIntOrNull(), monedas) { valor, dinero -> convertir(valor, dinero) }
            .orWarning(prefijo.name, valor)

private fun ElementoPistaDBO.toPistaFalsa(prefijo: Prefijo, monedas: Int): Pista.PistaFalsa? =
    valor?.takeIf { it == Pista.PistaFalsa.ID }?.let { Pista.PistaFalsa(prefijo, monedas = monedas) }

fun Pista.toDBO(idJugador: Long?, partida: Long) =
    ElementoPistaDBO(prefijo.id, valor, monedas, partida, idJugador)

private fun <T> T?.orWarning(tipo: String, valor: String?): T? {
    if (this == null) {
        Logger("ElementoPistaMapper").log("No se ha podido recrear una pista $tipo porque su" +
                " valor es $valor")
    }
    return this
}