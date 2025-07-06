package com.example.composetest.data.mapper.fromdbo

import com.example.composetest.data.db.dbo.JugadorDBO
import com.example.composetest.data.db.relations.JugadorCompleto
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Evento.Comodin
import com.example.composetest.model.Jugador

fun JugadorCompleto.toModel(): Jugador = Jugador(
    jugador.nombre,
    cartas.mapNotNull { it.toModel() }.toMutableList(),
    pistas.mapNotNull { it.toModel() }.toMutableList(),
    jugador.dinero,
    jugador.baremo,
    jugador.getIdsSecretosConocidosAsStringList().orEmpty(),
    jugador.getIdsSecretosConocidosRondaAsStringList().orEmpty(),
    jugador.comodines?.split("|")?.mapNotNull { Comodin.getById(it) }.orEmpty()
)

fun JugadorDBO.getIdsSecretosConocidosAsStringList(): List<String>? = idsSecretosConocidos?.split("|")

fun JugadorDBO.getIdsSecretosConocidosRondaAsStringList(): List<String>? = idsSecretosConocidosRonda?.split("|")

fun JugadorDBO.getIdSecretoParaGuardarSiAplica(pista: ElementoTablero.Pista): String? =
    pista
        .takeIf { it is ElementoTablero.Pista.Secreto }
        ?.id
        .takeIf { getIdsSecretosConocidosRondaAsStringList()?.contains(it) == true }

fun JugadorDBO.nuevoSecretoAdquirido(idSecreto: String): String = idsSecretosConocidosRonda.orEmpty().nuevoSecretoAdquirido(idSecreto)

/** Concatena con una | si ya hay algún contenido, o devuelve el [idSecreto]. */
fun String.nuevoSecretoAdquirido(idSecreto: String): String {
    val nexo = "|".takeIf { this.isNotBlank() }.orEmpty()
    return "$this$nexo$idSecreto"
}