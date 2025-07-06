package com.example.composetest.data.uc

import com.example.composetest.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class UC<PARAMETROS: UC.Parametros, RESULTADO> {

    protected val logger: Logger = Logger(obtenerTag())

    suspend operator fun invoke(parameters: PARAMETROS): RESULTADO = withContext(Dispatchers.Default) {
        execute(parameters)
    }

    abstract suspend fun execute(parametros: PARAMETROS): RESULTADO

    /** Público por si se quiere sobrescribir. */
    fun obtenerTag(): String = this::class.simpleName ?: "UseCase sin nombre"

    fun <RESULTADO> crearError(t: Throwable): Respuesta.Error<RESULTADO> {
        val mensaje = t.message ?: "Sin mensaje de error"
        logger.error(mensaje, t.cause)
        return Respuesta.Error(mensaje)
    }

    interface Parametros

    sealed class Respuesta<RESULTADO>(
        protected val valorInterno: RESULTADO?,
        protected val errorInterno: String?,
    ) {

        class Valor<RESULTADO>(valor: RESULTADO) : Respuesta<RESULTADO>(valor, null) {
            val valor: RESULTADO = valorInterno!!
        }

        class Error<RESULTADO>(error: String) : Respuesta<RESULTADO>(null, error) {
            val error: String = errorInterno!!
        }
    }
}

object None: UC.Parametros
