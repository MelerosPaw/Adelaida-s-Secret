package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class ComprobarCargaInicialUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
    private val initialDataLoaded: CountDownLatch
) : UC<None, Boolean>() {

    override suspend fun execute(parametros: None): Boolean = verificar()

    private suspend fun verificar(haEsperado: Boolean = false): Boolean {
        val (sospechosos, contenidos) = getDatosIniciales()

        return if (!haEsperado && sospechosos < 10 || contenidos < 40) {
            initialDataLoaded.await()
            verificar(true)
        } else {
            sospechosos == 10 && contenidos == 40
        }
    }

    private suspend fun getDatosIniciales(): Pair<Int, Int> {
        Logger.logSql("Verificar que están todos los sospechosos cargados")
        val sospechosos = baseDatos.sospechosoDao().verificarDatosIniciales()
        Logger.logSql("Verificar que están todos los contenidos de los sospechosos cargados")
        val contenidos = baseDatos.sospechosoYContenidoDao().verificarDatosIniciales()
        return Pair(sospechosos, contenidos)
    }
}