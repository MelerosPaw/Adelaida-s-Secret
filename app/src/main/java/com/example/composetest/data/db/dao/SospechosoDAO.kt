package com.example.composetest.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.composetest.data.db.dbo.SospechosoDBO
import com.example.composetest.data.db.relations.nm.SospechosoCompleto

@Dao
interface SospechosoDAO {

    @Insert
    suspend fun crearSospechosos(sospechosos: List<SospechosoDBO>)

    @Query("SELECT COUNT(SospechosoDBO.idSospechoso) FROM SospechosoDBO")
    suspend fun verificarDatosIniciales(): Int

    @Transaction
    @Query("SELECT * FROM SospechosoDBO")
    suspend fun obtenerSospechosos(): List<SospechosoCompleto>

    @Query("SELECT SospechosoDBO.idSospechoso FROM SospechosoDBO WHERE SospechosoDBO.nombre = :nombre")
    suspend fun obtenerId(nombre: String): Long?
}