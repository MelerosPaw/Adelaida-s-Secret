package com.example.composetest.data.db

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.composetest.Logger
import com.example.composetest.data.db.dbo.ContenidoPistaDBO
import com.example.composetest.data.db.dbo.EventoDBO
import com.example.composetest.data.db.dbo.SospechosoDBO
import com.example.composetest.data.db.relations.nm.SospechosoContenidoDBO
import com.example.composetest.di.DataModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.CountDownLatch

class CallbackCargaInicial(
    val latch: CountDownLatch,
    val context: Context
) : RoomDatabase.Callback() {

    private val logger = Logger(CallbackCargaInicial::class.java.name)

    /**
     * Solo se llamará en dos ocasiones: la primera vez que se instale la aplicación y, si
     * el usuario de alguna manera borra la base de datos (porque se tendrá que volver a crear). La
     * base de datos no se borra automáticamente cuando se desinstala; se queda en el teléfono por
     * si se vuelve a instalar, para que el usuario no pierda los datos. Por lo tanto, si el usuario
     * desinstala y vuelve a instalar, los datos ya están creados, por lo que el latch de
     * [com.example.composetest.data.uc.ComprobarCargaInicialUC] no llamará a await() y no
     * necesitamos que se ejecute este bloque.
     */
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        cargarDatos()
    }

    /**
     * Mientras tengamos la destrucción por migración, siempre que pase por aquí, borrará todos los
     * datos, por lo que necesitamos que se vuelvan a cargar, o se quedará la aplicación pillada
     * cuando el caso de uso [com.example.composetest.data.uc.ComprobarCargaInicialUC] llame al
     * await() del latch.
     */
    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
        super.onDestructiveMigration(db)
        cargarDatos()
    }

    private fun cargarDatos() {
        CoroutineScope(Dispatchers.IO).launch {
            DataModule.database?.run {
                listOf(
                    async { cargarSospechosos() },
                    async { cargarContenidoPistas() },
                    async { cargarEventos() }
                ).awaitAll()
                latch.countDown()
            }
        }
    }

    private suspend fun AdelaidaDatabase.cargarContenidoPistas() {
        val cantidadColumnasEsperadas = 4
        val nombreArchivo = "TextoPistas.csv"
        leerLineas(nombreArchivo, "|", { fila, columnas ->
                if (columnas.size != cantidadColumnasEsperadas) {
                    logCantidadColumnasErronea(fila, nombreArchivo, columnas, cantidadColumnasEsperadas)
                    null
                } else {
                    ContenidoPistaDBO(
                        id = columnas[0],
                        idSecretoVinculado = columnas[1].takeIf { it.isNotBlank() },
                        texto = columnas[2],
                        textoEnLibro = columnas[3]
                    )
                }
            })
            ?.let {
                Logger.logSql("Crear pistas de $nombreArchivo")
                contenidoPistaDao().crearContenidos(it)
            }
    }

    private suspend fun AdelaidaDatabase.cargarSospechosos() {
        val cantidadColumnasEsperadas = 6
        val nombreArchivo = "PersonajeIdPista.csv"
        leerLineas(
            nombreArchivoAssets = nombreArchivo,
            separadorColumnas = ",",
            transformacionRegistro = { fila, columnas ->
                if (columnas.size != cantidadColumnasEsperadas) {
                    logCantidadColumnasErronea(fila, nombreArchivo, columnas, cantidadColumnasEsperadas)
                    null
                } else {
                    val rasgosYSecreto = listOf(
                        SospechosoContenidoDBO(fila, columnas[1]),
                        SospechosoContenidoDBO(fila, columnas[2]),
                        SospechosoContenidoDBO(fila, columnas[3]),
                        SospechosoContenidoDBO(fila, columnas[4])
                    )
                    SospechosoDBO(fila, columnas[0], columnas[5]) to rasgosYSecreto
                }
            })
            ?.associate { it.first to it.second }
            ?.let { contenido ->
                coroutineScope {
                    async {
                        Logger.logSql("Crear sospechoso de $nombreArchivo")
                        sospechosoDao().crearSospechosos(contenido.map { it.key })
                    }
                    async {
                        Logger.logSql("Crear sospechoso y contenido de $nombreArchivo")
                        sospechosoYContenidoDao().crearSospechososContenidos(contenido.flatMap { it.value })
                    }
                }
            }
    }

    private fun AdelaidaDatabase.cargarEventos() {
        val nombreArchivo = "Eventos.csv"
        val cantidadColumnasEsperadas = 5

        leerLineas(
            nombreArchivoAssets = nombreArchivo,
            separadorColumnas = "|",
            saltarCabecera = true,
            transformacionRegistro = { fila, columnas ->
                if (cantidadColumnasEsperadas != columnas.size) {
                    logCantidadColumnasErronea(fila, nombreArchivo, columnas, cantidadColumnasEsperadas)
                    null
                } else {
                    EventoDBO(
                        id = fila,
                        ronda = columnas[1],
                        nombre = columnas[0],
                        explicacion = columnas[2],
                        maxGanadores = columnas[3],
                        idAccion = columnas[4]
                    )
                }
            }
        )?.let {
            Logger.logSql("Crear eventos de $nombreArchivo")
            eventoDao().insertarEventos(it)
        }
    }

    private fun logCantidadColumnasErronea(
        fila: Int,
        nombreArchivo: String,
        columnas: List<String>,
        cantidadColumnasEsperadas: Int
    ) {
        logger.error(
            "La fila ${fila.inc()} del archivo de $nombreArchivo contiene ${columnas.size} "
                + "columnas en lugar de $cantidadColumnasEsperadas."
        )
    }

    /**
     * @return Devulve `null` si hay algún fallo al leer el archivo, o una lista con todos los
     * objetos no nulos resultantes de aplicar [transformacionRegistro] a cada fila del archivo.
     */
    private fun <T> leerLineas(
        nombreArchivoAssets: String,
        separadorColumnas: String,
        transformacionRegistro: (fila: Int, columnas: List<String>) -> T?,
        saltarCabecera: Boolean = true
    ): List<T>? {
        val inputStream = try {
            context.assets.open(nombreArchivoAssets)
        } catch (e: IOException) {
            logger.error("No se ha podido abrir el archivo $nombreArchivoAssets", e)
            null
        }

        return inputStream?.let {
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.useLines { lines ->
                lines.mapIndexedNotNull { index, line ->
                    line.takeIf { !saltarCabecera || index > 0 }
                        ?.let { transformacionRegistro(index, line.split(separadorColumnas)) }
                }.toList()
            }
        }
    }
}