package com.example.composetest.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.composetest.data.db.dbo.ContenidoPistaDBO

@Dao
interface ContenidoPistaDAO {

    @Insert
    suspend fun crearContenidos(contenido: List<ContenidoPistaDBO>)

    @Query("SELECT COUNT(*) FROM ContenidoPistaDBO")
    suspend fun verificarContenido(): Int

    @Query("SELECT * FROM ContenidoPistaDBO WHERE ContenidoPistaDBO.idPista = :idPista")
    fun obtener(idPista: String): ContenidoPistaDBO?
}