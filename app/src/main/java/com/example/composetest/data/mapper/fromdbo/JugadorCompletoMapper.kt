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

private fun JugadorDBO.getIdsSecretosConocidosAsStringList(): List<String>? = idsSecretosConocidos?.split("|")

fun JugadorDBO.getIdsSecretosConocidosRondaAsStringList(): List<String>? = idsSecretosConocidosRonda?.split("|")

fun JugadorDBO.getIdSecretoParaGuardarSiAplica(pista: ElementoTablero.Pista): String? =
    pista.id
        .takeIf { pista is ElementoTablero.Pista.Secreto }
        .takeIf { it !in getIdsSecretosConocidosRondaAsStringList().orEmpty() }

fun JugadorDBO.nuevoSecretoAdquirido(idSecreto: String): JugadorDBO = copy(
    idsSecretosConocidosRonda = idsSecretosConocidosRonda.orEmpty().nuevoSecretoAdquirido(idSecreto)
)

/** Concatena con una | si ya hay algún contenido, o devuelve el [idSecreto]. */
fun String.nuevoSecretoAdquirido(idSecreto: String): String {
    val nexo = "|".takeIf { this.isNotBlank() }.orEmpty()
    return "$this$nexo$idSecreto"
}

fun JugadorDBO.eliminarSecreto(idSecreto: String) = copy(
    idsSecretosConocidosRonda = getIdsSecretosConocidosRondaAsStringList()
        ?.filter { it != idSecreto }
        ?.joinToString("|") { it })