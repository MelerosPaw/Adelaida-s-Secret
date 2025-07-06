package com.example.composetest.data.mapper.fromdbo

import com.example.composetest.data.db.dbo.ElementoCartaDBO
import com.example.composetest.data.db.dbo.ElementoPistaDBO
import com.example.composetest.data.db.dbo.ElementoTableroDBO
import com.example.composetest.model.ElementoTablero

fun ElementoTableroDBO.toModel(): ElementoTablero? {
    val prefijoPista = ElementoTablero.Pista.Prefijo.fromId(prefijo)
    val prefijoCarta = ElementoTablero.Carta.Prefijo.fromId(prefijo)

    return when {
        prefijoPista != null -> (this as ElementoPistaDBO).toModel()
        prefijoCarta != null -> (this as ElementoCartaDBO).toModel()
        else -> null
    }
}