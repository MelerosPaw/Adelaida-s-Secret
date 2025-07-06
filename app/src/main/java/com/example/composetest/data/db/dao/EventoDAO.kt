package com.example.composetest.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.composetest.data.db.dbo.EventoDBO

@Dao
interface EventoDAO {

    @Query("SELECT * FROM EventoDBO")
    fun obtenerTodos(): List<EventoDBO>

    @Query("SELECT * FROM EventoDBO WHERE EventoDBO.nombre = :nombre LIMIT 1")
    fun obtenerPorNombre(nombre: String): EventoDBO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarEventos(eventos: List<EventoDBO>)
}