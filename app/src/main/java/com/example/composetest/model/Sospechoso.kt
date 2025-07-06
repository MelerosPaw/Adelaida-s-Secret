package com.example.composetest.model

import java.util.Collections

class Sospechoso(
    val nombre: String,
    val rasgos: Array<Rasgo>,
    val secreto: Secreto,
    val genero: Genero
) {

    override fun toString(): String = "$nombre - ${descripcionRasgos()}"

    fun descripcionRasgos(): String = "Rasgos: ${rasgos.joinToString { it.idPista }}\n" +
            "Secreto: ${secreto.idPista} (asociado con el ${secreto.idSecretoVinculado})"

    fun identificadoresPistas(): List<String> {
        val todos = rasgos.map { it.idPista } + secreto.idPista
        return secreto.idSecretoVinculado?.let { todos + Collections.singletonList(it) } ?: todos
    }

    enum class Genero(val id: String) {
        HOMBRE("H"),
        MUJER("M");

        companion object {

            fun getById(id: String): Genero = entries.firstOrNull { it.id == id } ?: HOMBRE
        }
    }
}