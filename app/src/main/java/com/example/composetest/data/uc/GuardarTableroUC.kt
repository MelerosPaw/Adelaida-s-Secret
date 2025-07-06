package com.example.composetest.data.uc

import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.dbo.CasillaDBO
import com.example.composetest.data.db.dbo.ElementoCartaDBO
import com.example.composetest.data.db.dbo.ElementoPistaDBO
import com.example.composetest.data.db.dbo.TableroDBO
import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.data.mapper.fromdbo.toModel
import com.example.composetest.data.uc.UC.Respuesta.Valor
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Partida
import com.example.composetest.model.Tablero
import javax.inject.Inject

typealias ElementoUbicado = Pair<ElementoTablero, String>

class GuardarTableroUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
): UC<GuardarTableroUC.Parametros, UC.Respuesta<Partida>>() {

    class Parametros(
        val idPartida: Long,
        val tablero: Tablero,
    ): UC.Parametros

    override suspend fun execute(parametros: Parametros): Respuesta<Partida> = with(parametros) {
        val tableroDBO = TableroDBO(idPartida, tablero.cantidadColumnas, tablero.cantidadFilas)
        val casillas: List<CasillaDBO> = tablero.mapCasillas(idPartida)
        val elementos: List<ElementoUbicado> = tablero.mapElementos(idPartida)
        val pistas: List<ElementoPistaDBO> = elementos.mapPistas(idPartida)
        val cartas: List<ElementoCartaDBO> = elementos.mapCartas(idPartida)

        return try {
            baseDatos.tableroDao().crearTablero(tableroDBO, casillas, pistas, cartas)
            baseDatos.partidaDao().obtenerPartidaOThrow(idPartida).toModel()
                ?.let(::Valor)
                ?: Respuesta.Error("No se ha podido obtener la partida tras haberla actualizado")
        } catch (e: BaseDatosException) {
            crearError(e)
        }
    }

    private fun Tablero.mapCasillas(idPartida: Long) = habitaciones
        .flatMap { it.casillas }
        .map { CasillaDBO(it.fila, it.columna, idPartida) }

    private fun Tablero.mapElementos(idPartida: Long): List<ElementoUbicado> = habitaciones
        .flatMap { it.casillas }
        .mapNotNull { casilla ->
            casilla.contenido?.let {
                it to "${casilla.fila}${casilla.columna}${idPartida}"
            }
        }

    private fun List<ElementoUbicado>.mapPistas(idPartida: Long): List<ElementoPistaDBO> =
        mapNotNull { elemento ->
            (elemento.first as? ElementoTablero.Pista)?.let {
                ElementoPistaDBO(it.prefijo.id, it.valor, it.monedas, idPartida, null, elemento.second)
            }
        }

    private fun List<ElementoUbicado>.mapCartas(idPartida: Long): List<ElementoCartaDBO> =
        mapNotNull { elemento ->
            (elemento.first as? ElementoTablero.Carta)?.let {
                val monedas = (it as? ElementoTablero.Carta.Dinero)?.monedas
                ElementoCartaDBO(it.prefijo.id, it.valor, idPartida, monedas, null, elemento.second)
            }
        }
}