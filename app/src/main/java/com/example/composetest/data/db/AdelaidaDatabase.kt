package com.example.composetest.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composetest.data.db.dao.AsignarElementoDAO
import com.example.composetest.data.db.dao.ContenidoPistaDAO
import com.example.composetest.data.db.dao.EventoDAO
import com.example.composetest.data.db.dao.JugadorDAO
import com.example.composetest.data.db.dao.PartidaDAO
import com.example.composetest.data.db.dao.SospechosoDAO
import com.example.composetest.data.db.dao.SospechosoYContenidoDAO
import com.example.composetest.data.db.dao.TableroDAO
import com.example.composetest.data.db.dbo.CasillaDBO
import com.example.composetest.data.db.dbo.ComodinDBO
import com.example.composetest.data.db.dbo.ContenidoPistaDBO
import com.example.composetest.data.db.dbo.ElementoCartaDBO
import com.example.composetest.data.db.dbo.ElementoPistaDBO
import com.example.composetest.data.db.dbo.EventoDBO
import com.example.composetest.data.db.dbo.JugadorDBO
import com.example.composetest.data.db.dbo.PartidaDBO
import com.example.composetest.data.db.dbo.SospechosoDBO
import com.example.composetest.data.db.dbo.TableroDBO
import com.example.composetest.data.db.relations.nm.EventoPartidaDBO
import com.example.composetest.data.db.relations.nm.SospechosoContenidoDBO

@Database(
    entities = [
        JugadorDBO::class, ElementoCartaDBO::class, ElementoPistaDBO::class, PartidaDBO::class,
        TableroDBO::class, CasillaDBO::class, ContenidoPistaDBO::class, SospechosoDBO::class,
        SospechosoContenidoDBO::class, EventoDBO::class, EventoPartidaDBO::class
    ],
    version = 5)
abstract class AdelaidaDatabase : RoomDatabase() {
    abstract fun contenidoPistaDao(): ContenidoPistaDAO
    abstract fun sospechosoDao(): SospechosoDAO
    abstract fun sospechosoYContenidoDao(): SospechosoYContenidoDAO
    abstract fun partidaDao(): PartidaDAO
    abstract fun tableroDao(): TableroDAO
    abstract fun asignarElementoDao(): AsignarElementoDAO
    abstract fun jugadorDao(): JugadorDAO
    abstract fun eventoDao(): EventoDAO
}