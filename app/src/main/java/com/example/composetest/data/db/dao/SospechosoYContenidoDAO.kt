package com.example.composetest.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.composetest.data.db.relations.nm.SospechosoContenidoDBO

@Dao
interface SospechosoYContenidoDAO {

    @Insert
    suspend fun crearSospechososContenidos(sospechososContenidos: List<SospechosoContenidoDBO>)

    @Query("SELECT COUNT(SospechosoContenidoDBO.idSospechoso) FROM SospechosoContenidoDBO")
    suspend fun verificarDatosIniciales(): Int
}