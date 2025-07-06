package com.example.composetest.data.db.exception

open class BaseDatosException(val mensaje: String, cause: Throwable?) : Exception(mensaje, cause)