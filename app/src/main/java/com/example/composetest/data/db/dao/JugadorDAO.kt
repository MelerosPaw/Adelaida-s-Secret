package com.example.composetest.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.composetest.data.db.dbo.JugadorDBO
import com.example.composetest.data.db.relations.JugadorCompleto
import kotlinx.coroutines.flow.Flow

@Dao
interface JugadorDAO {

    @Transaction
    @Query("SELECT * FROM JugadorDBO WHERE JugadorDBO.partida = :partida")
    fun obtenerPorPartida(partida: Long): Flow<List<JugadorCompleto>>

    @Query("UPDATE JugadorDBO SET nombre = :nombreNuevo " +
           "WHERE JugadorDBO.nombre = :nombreAnterior AND JugadorDBO.partida = :idPartida")
    fun cambiarNombre(nombreAnterior: String, idPartida: Long, nombreNuevo: String)

    @Query("SELECT * FROM JugadorDBO WHERE JugadorDBO.partida = :idPartida AND JugadorDBO.nombre = :nombreJugador")
    fun obtener(idPartida: Long, nombreJugador: String): JugadorCompleto?

    @Update
    fun actualizar(jugador: JugadorDBO): Int
}