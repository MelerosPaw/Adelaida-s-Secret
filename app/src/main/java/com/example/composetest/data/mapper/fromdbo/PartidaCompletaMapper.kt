package com.example.composetest.data.mapper.fromdbo

import com.example.composetest.Logger
import com.example.composetest.data.db.relations.PartidaCompleta
import com.example.composetest.extensions.formatearFecha
import com.example.composetest.extensions.procesarFecha
import com.example.composetest.model.Partida

fun PartidaCompleta.toModel(): Partida? = procesarFecha(partida.fecha)?.let { fecha ->
    Partida(
        id = partida.id,
        estadoCreacion = Partida.EstadoCreacion.byId(partida.idEstado),
        jugadores = jugadores.map { it.toModel() }.toTypedArray(),
        tablero = tablero?.toModel(),
        fecha = fecha,
        nombre = partida.nombre ?: formatearFecha(fecha),
        asesino = asesino?.toModel(),
        ronda = Partida.Ronda.byId(partida.idRonda),
        dia = partida.dia,
        fuerzaDefensa = partida.fuerzaDefensa,
        eventoActual = eventoActual?.toModel(),
        eventoActualEjecutado = partida.eventoActualEjecutado,
        eventosConsumidos = eventosConsumidos.mapNotNull { it.toModel() }.toSet(),
    )
} ?: run {
    Logger("PartidaCompletaMapper")
        .error("No se ha podido recuperar una partida porque la fecha estaba mal:" +
                "\n\tNombre: ${partida.nombre}" +
                "\n\tFecha: ${partida.fecha}" +
                "\n\tJugadores: ${jugadores.joinToString { it.jugador.nombre }}" +
                "\n\tEstado: ${partida.idEstado}"
        )
    null
}