package com.example.composetest.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.composetest.Logger
import com.example.composetest.data.db.dao.PartidaDAO.CrearPartidaException.JugadoresNoCreados
import com.example.composetest.data.db.dao.PartidaDAO.CrearPartidaException.JugadoresRepetidos
import com.example.composetest.data.db.dao.PartidaDAO.CrearPartidaException.PartidaRepetida
import com.example.composetest.data.db.dao.PartidaDAO.CrearPartidaException.ReputacionesNoGuardadas
import com.example.composetest.data.db.dao.PartidaDAO.ObtenerPartidaException.PartidaInexistente
import com.example.composetest.data.db.dbo.ElementoCartaDBO
import com.example.composetest.data.db.dbo.JugadorDBO
import com.example.composetest.data.db.dbo.PartidaDBO
import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.data.db.reduced.PartidaConRonda
import com.example.composetest.data.db.reduced.PartidaYAsesino
import com.example.composetest.data.db.reduced.SoloNombrePartida
import com.example.composetest.data.db.relations.PartidaCompleta
import com.example.composetest.data.mapper.fromdbo.toDBO
import com.example.composetest.extensions.joinToStringHumanReadable
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.ElementoTablero
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDAO {

    /**
     * No se pueden crear partidas con el mismo nombre. En caso de hacerlo, fallará para revertir la
     * transacción.
     */
    @Insert
    suspend fun crear(partida: PartidaDBO): Long

    /**
     * Al crear jugadores, estos deben ir asociados a una partida. En una misma partida no puede
     * haber más de un jugador con el mismo nombre. En caso de que lo haya, fallará para revertir
     * la transacción.
     */
    @Insert
    suspend fun guardarJugadores(jugadores: List<JugadorDBO>): List<Long>

    @Insert
    suspend fun guardarCartas(elementos: List<ElementoCartaDBO>): List<Long>

    @Transaction
    @Query("SELECT * FROM PartidaDBO WHERE PartidaDBO.nombre = :nombre LIMIT 1")
    suspend fun obtenerPorNombre(nombre: String): PartidaCompleta?

    @Transaction
    @Query("SELECT * FROM PartidaDBO WHERE PartidaDBO.id = :idPartida LIMIT 1")
    fun obtener(idPartida: Long): Flow<PartidaCompleta>

    @Query("SELECT COUNT(id) FROM PartidaDBO WHERE PartidaDBO.id = :idPartida")
    fun cuantas(idPartida: Long): Int

    @Transaction
    @Query("SELECT PartidaDBO.nombre FROM PartidaDBO WHERE PartidaDBO.nombre = :nombre LIMIT 1")
    suspend fun obtenerNombrePartida(nombre: String): SoloNombrePartida?

    @Transaction
    @Query("SELECT * FROM PartidaDBO WHERE PartidaDBO.id = :idPartida LIMIT 1")
    suspend fun obtenerPartida(idPartida: Long): PartidaCompleta?

    @Update(entity = PartidaDBO::class)
    suspend fun actualizarRondaPartida(partida: PartidaConRonda): Int

    @Transaction
    suspend fun obtenerPartidaOThrow(idPartida: Long): PartidaCompleta {
        Logger.logSql("Obtener una partida")
        return obtenerPartida(idPartida) ?: throw PartidaInexistente(idPartida)
    }

    @Transaction
    @Query("SELECT * FROM PartidaDBO")
    fun obtenerTodas(): Flow<List<PartidaCompleta>>

    @Query("UPDATE PartidaDBO SET nombre = :nombreNuevo WHERE PartidaDBO.id = :idPartida")
    suspend fun actualizarNombrePartida(idPartida: Long, nombreNuevo: String): Int

    /**
     * Las excepciones lanzadas aquí dentro no son necesarias para cancelar la transacción, ya
     * que las operaciones llamadas ya lanzan excepciones. Aunque controladas, ya sirven para
     * que se cancele la excepción automáticamente. Pero lanzamos excepciones para salir del método
     * de forma informativa sin tener que cambiar el tipo de retorno.
     */
    @Transaction
    suspend fun crearPartida(partida: PartidaDBO): Long = try {
        Logger.logSql("Crear una partida")
        crear(partida)
    } catch (e: Exception) {
        throw PartidaRepetida(e, partida)
    }

    @Update(onConflict = OnConflictStrategy.Companion.IGNORE, entity = PartidaDBO::class)
    suspend fun asignarAsesino(actualizacion: PartidaYAsesino): Int

    @Transaction
    suspend fun asignarJugadores(jugadores: List<JugadorDBO>, idPartida: Long) {
        coroutineScope {
            val idsJugadores = crearJugadores(jugadores)
            val reputaciones = crearReputaciones(jugadores, idsJugadores, idPartida)
            guardarReputaciones(reputaciones)
        }
    }

    private suspend fun crearJugadores(jugadores: List<JugadorDBO>): List<Long> {
        val idsJugadores = try {
            Logger.logSql("Crear los jugadores de una partida")
            val ids = guardarJugadores(jugadores)
            ids.takeIf { -1L !in ids || jugadores.size == ids.size }
                ?: throw JugadoresNoCreados(jugadores, ids)
        } catch (e: Exception) {
            throw JugadoresRepetidos(e)
        }
        return idsJugadores
    }

    private fun crearReputaciones(
        jugadores: List<JugadorDBO>,
        idsJugadores: List<Long>,
        idPartida: Long
    ): List<ElementoCartaDBO> = jugadores.mapIndexed { index, jugador ->
        ElementoTablero.Carta.Reputacion(jugador.nombre).toDBO(idsJugadores[index], idPartida)
    }

    private suspend fun guardarReputaciones(reputaciones: List<ElementoCartaDBO>): List<Long> = try {
        Logger.logSql("Guardar las reputaciones al crear una partida")
        guardarCartas(reputaciones)
    } catch (e: Throwable) {
        throw ReputacionesNoGuardadas(e, reputaciones)
    }

    // TODO Melero: 26/11/24 Hay que decidir qué se debe borrar en base al estado de creación
    @Transaction
    suspend fun borrar(id: Long) {
        borrarJugadores(id)
        borrarCasillas(id)
        borrarTablero(id)
        borrarElementos(id)
        borraLaPartida(id)
    }

    suspend fun borraLaPartida(id: Long) {
        try {
            Logger.logSql("Borrar una partida")
            // TODO Melero: 24/1/25 Esto tendría que ir en cascada y que se borre solo todo lo demás
            val borradas = borrarPartida(id)
            if (borradas != 1) {
                throw PartidaNoBorrada(borradas, null)
            }
        } catch (e: Exception) {
            throw e.takeIf { it is PartidaNoBorrada } ?: PartidaNoBorrada(null, e)
        }
    }

    fun borrarElementos(id: Long) {
        try {
            Logger.logSql("Borrar las cartas de una partida")
            val cartasBorradas = borrarCartasDePartida(id)
            Logger.logSql("Borrar las pistas de una partida")
            val pistasBorradas = borrarPistasDePartida(id)

    //            if (tableroBorrado == 1 && cartasBorradas + pistasBorradas != 64) {
    //                throw ElementosNoBorrados(cartasBorradas, pistasBorradas, null)
    //            }
        } catch (e: Exception) {
            throw e.takeIf { it is ElementosNoBorrados } ?: ElementosNoBorrados(null, null, e)
        }
    }

    fun borrarTablero(id: Long) {
        try {
            Logger.logSql("Borrar el tablero de una partida")
            val borrado = borrarTableroDePartida(id)

            if (borrado > 1) {
                throw TableroNoBorrado(borrado, null)
            }
        } catch (e: Exception) {
            throw e.takeIf { it is TableroNoBorrado } ?: TableroNoBorrado(null, e)
        }
    }

    fun borrarJugadores(id: Long) {
        try {
            Logger.logSql("Borrar los jugadores de una partida")
            val borrados = borrarJugadoresDePartida(id)

//            if (borrados < 2) {
//                throw JugadoresNoBorrados(borrados, null)
//            }
        } catch (e: Exception) {
            throw e.takeIf { it is JugadoresNoBorrados } ?: JugadoresNoBorrados(null, e)
        }
    }

    fun borrarCasillas(id: Long) {
        try {
            Logger.logSql("Borrar casilla de una partida")
            val borrado = borrarCasillasDePartida(id)

            if (borrado != 64) {
                throw CasillasNoBorradas(borrado, null)
            }
        } catch (e: Exception) {
            throw e.takeIf { it is CasillasNoBorradas } ?: CasillasNoBorradas(null, e)
        }
    }

    @Query("DELETE FROM PartidaDBO WHERE PartidaDBO.id = :id")
    suspend fun borrarPartida(id: Long): Int

    @Query("DELETE FROM JugadorDBO WHERE JugadorDBO.partida = :partida")
    fun borrarJugadoresDePartida(partida: Long): Int

    @Query("DELETE FROM ElementoCartaDBO WHERE ElementoCartaDBO.partida = :partida")
    fun borrarCartasDePartida(partida: Long): Int

    @Query("DELETE FROM ElementoPistaDBO WHERE ElementoPistaDBO.partida = :partida")
    fun borrarPistasDePartida(partida: Long): Int

    @Query("DELETE FROM TableroDBO WHERE TableroDBO.id = :partida")
    fun borrarTableroDePartida(partida: Long): Int

    @Query("DELETE FROM CasillaDBO WHERE CasillaDBO.id = :partida")
    fun borrarCasillasDePartida(partida: Long): Int

    @Query("UPDATE PartidaDBO SET idEvento = :eventoId, eventoActualEjecutado = :ejecutado WHERE PartidaDBO.id = :idPartida")
    fun cambiarEventoActual(idPartida: Long, eventoId: Int, ejecutado: Boolean): Int

    @Query("UPDATE PartidaDBO SET eventoActualEjecutado = :seHaEjecutado WHERE PartidaDBO.id = :idPartida")
    fun actualizarEventoEjecutado(idPartida: Long, seHaEjecutado: Boolean): Int

  sealed class ObtenerPartidaException(mensaje: String): BaseDatosException(mensaje, null) {

        class PartidaInexistente(idPartida: Long): ObtenerPartidaException(
            "No se ha podido recuperar la partida con id $idPartida")
    }

    sealed class CrearPartidaException(mensaje: String, causa: Throwable?): BaseDatosException(mensaje, causa) {

        class PartidaRepetida(causa: Exception, partida: PartidaDBO) : CrearPartidaException(
            "Ya existe una partida llamada ${partida.nombre}. Escoge otro nombre.", causa
        )

        class JugadoresNoCreados(jugadores: List<JugadorDBO>, ids:List<Long>) :
            CrearPartidaException(
                "No se ha podido crear estos jugadores: ${jugadores.joinToStringHumanReadable { it.toString() }}.\n" +
                        "Les correspondían estos ids: ${ids.joinToStringHumanReadable { it.toString() }}.",
                null
            )

        class JugadoresRepetidos(causa: Exception?) :
            CrearPartidaException("No puede haber jugadores repetidos. Revisa los nombres.", causa)

        class ReputacionesNoGuardadas(causa: Throwable, reputaciones: List<ElementoCartaDBO>) :
            CrearPartidaException(
                "No se han podido guardar las reputaciones de cada jugador:n \n"
                        + reputaciones.joinToString("\n", transform = ElementoCartaDBO::info), causa
            )
    }

    abstract class BorrarPartidaException(val mensaje: String, causa: Exception?): Exception(causa)

    class JugadoresNoBorrados(cantidadBorrados: Int?, causa: Exception?)
        : BorrarPartidaException(cantidadBorrados
        ?.let { "Error: se han borrado $it jugodres" }
        ?: "No se ha borrado la partida", causa)

    class ElementosNoBorrados(cantidadPistas: Int?, cantidadCartas: Int?, causa: Exception?) :
        BorrarPartidaException(
            noneNull(cantidadPistas, cantidadCartas) { pistas, cartas ->
                "Se han borrado $pistas pistas y $cartas cartas"
            } ?: "No se han borrado las pistas y cartas", causa)

    class TableroNoBorrado(cantidadBorrados: Int?, causa: Exception?) :
        BorrarPartidaException(cantidadBorrados
            ?.let { "Error: se han borrado $it tableros" }
            ?: "No se ha borrado el tablero", causa)


    class CasillasNoBorradas(cantidadBorrados: Int?, causa: Exception?) :
        BorrarPartidaException(cantidadBorrados
            ?.let { "Error: no se han borrado todas las casillas, tan solo $it" }
            ?: "No se han borrado todas las casillas", causa)

    class PartidaNoBorrada(cantidadBorrados: Int?, causa: Exception?)
        : BorrarPartidaException(cantidadBorrados
        ?.let { "Error: se han borrado $cantidadBorrados partidas" }
        ?: "No se ha borrado la partida", causa)
}