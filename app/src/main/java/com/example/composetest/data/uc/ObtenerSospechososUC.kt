package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.relations.nm.SospechosoCompleto
import com.example.composetest.data.mapper.fromdbo.toModel
import com.example.composetest.model.Sospechoso
import javax.inject.Inject

class ObtenerSospechososUC @Inject constructor(
    private val baseDatos: AdelaidaDatabase,
) : UC<None, List<Sospechoso>>() {

    override suspend fun execute(parametros: None): List<Sospechoso> {
        Logger.logSql("Obtener todos los sospechosos")
        return baseDatos.sospechosoDao().obtenerSospechosos()
            .map(SospechosoCompleto::toModel)
    }
}