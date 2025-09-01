package com.example.composetest.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.composetest.Logger
import com.example.composetest.data.db.dbo.CasillaDBO
import com.example.composetest.data.db.dbo.ElementoCartaDBO
import com.example.composetest.data.db.dbo.ElementoPistaDBO
import com.example.composetest.data.db.dbo.PartidaDBO
import com.example.composetest.data.db.dbo.TableroDBO
import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.data.db.reduced.PartidaConEstado
import com.example.composetest.data.db.reduced.PartidaConRonda
import com.example.composetest.data.db.relations.TableroCompleto
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@Dao
interface TableroDAO {

    @Transaction
    suspend fun crearTablero(
        tablero: TableroDBO,
        casillas: List<CasillaDBO>,
        pistas: List<ElementoPistaDBO>,
        cartas: List<ElementoCartaDBO>,
    ): Long = coroutineScope {
        verificarCantidadCasillas(tablero, casillas, pistas, cartas)

        Logger.logSql("Crear un tablero")
        val tableroDef = async { guardarTablero(tablero) }

        Logger.logSql("Crear las casillas de un tablero")
        val casillasDef = async {
            val casillasGuardadas = guardarCasillas(casillas)

            if (casillasGuardadas.size != casillas.size) {
                throw CasillasNoCreadas(casillasGuardadas.size, casillas)
            }
        }

        val pistasDef = async {
            Logger.logSql("Guardar pistas de una partida")
            val pistasGuardadas = guardarPistas(pistas)

            if (pistasGuardadas.size != pistas.size) {
                throw PistasNoCreadas(pistasGuardadas.size, pistas)
            }
        }

        val cartasDef = async {
            Logger.logSql("Guardar cartas de una partida")
            val cartasGuardadas = guardarCartas(cartas)

            if (cartasGuardadas.size != cartas.size) {
                throw CartasNoCreadas(cartasGuardadas.size, cartas)
            }
        }

        listOf(tableroDef, casillasDef, pistasDef, cartasDef).awaitAll()
        tablero.idPartida
    }

    fun verificarCantidadCasillas(
        tablero: TableroDBO,
        casillas: List<CasillaDBO>,
        pistas: List<ElementoPistaDBO>,
        cartas: List<ElementoCartaDBO>
    ) {
        val cantidadCasillas = tablero.cantidadFilas * tablero.cantidadColumnas

        when {
            cantidadCasillas != casillas.size ->
                throw NoCoincideCasillasConTablero(cantidadCasillas, casillas.size)

            cantidadCasillas != pistas.size + cartas.size ->
                throw NoCoincideElementosConTablero(cantidadCasillas, pistas.size + cartas.size)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTablero(tablero: TableroDBO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarCasillas(casillas: List<CasillaDBO>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarPistas(elementos: List<ElementoPistaDBO>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarCartas(elementos: List<ElementoCartaDBO>): List<Long>

    @Update(entity = PartidaDBO::class)
    suspend fun actualizarEstadoPartida(partida: PartidaConEstado): Int

    @Transaction
    @Query("SELECT * FROM TableroDBO WHERE TableroDBO.id = :idPartida")
    suspend fun obtenerTablero(idPartida: Long): TableroCompleto?

    abstract class CrearTableroException(mensaje: String, causa: Throwable?)
        : BaseDatosException(mensaje, causa)

    class NoCoincideCasillasConTablero(
        casillasTablero: Int,
        cantidadCasillas: Int
    ) : CrearTableroException("El tablero tiene $casillasTablero casillas, pero se están creando $cantidadCasillas", null)

    class NoCoincideElementosConTablero(
        casillasTablero: Int,
        cantidadElementos: Int
    ) : CrearTableroException("El tablero tiene $casillasTablero casillas, pero hay $cantidadElementos elementos", null)

    class CasillasNoCreadas(cantidadGuardadas: Int, casillas: List<CasillaDBO>)
        : CrearTableroException("Solo se han guardado $cantidadGuardadas de ${casillas.size} casillas. Estas eran las casillas que había que guardar:\n"
            + casillas.joinToString("\n", transform = CasillaDBO::info), null)

    class PistasNoCreadas(cantidadGuardadas: Int, pistas: List<ElementoPistaDBO>)
        : CrearTableroException("Solo se han guardado $cantidadGuardadas de ${pistas.size} pistas. Estas eran las pistas que había que guardar:\n"
            + pistas.joinToString("\n", transform = ElementoPistaDBO::info), null)

    class CartasNoCreadas(cantidadGuardadas: Int, cartas: List<ElementoCartaDBO>)
        : CrearTableroException("Solo se han guardado $cantidadGuardadas de ${cartas.size} cartas. Estas eran las cartas que había que guardar:\n"
            + cartas.joinToString("\n", transform = ElementoCartaDBO::info), null)
}