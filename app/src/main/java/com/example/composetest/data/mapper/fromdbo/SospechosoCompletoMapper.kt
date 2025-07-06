package com.example.composetest.data.mapper.fromdbo

import com.example.composetest.data.db.relations.nm.SospechosoCompleto
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Rasgo
import com.example.composetest.model.Secreto
import com.example.composetest.model.Sospechoso
import com.example.composetest.model.Sospechoso.Genero

fun SospechosoCompleto.toModel(): Sospechoso {
    val secreto = pistas.first { it.id.startsWith(ElementoTablero.Pista.Prefijo.SECRETO.id) }
    val rasgos = pistas - secreto

    return Sospechoso(
        sospechoso.nombre,
        rasgos.map { Rasgo(it.id, it.texto, it.textoEnLibro) }.toTypedArray(),
        Secreto(secreto.id, secreto.idSecretoVinculado, secreto.texto, secreto.textoEnLibro),
        Genero.getById(sospechoso.genero)
    )
}