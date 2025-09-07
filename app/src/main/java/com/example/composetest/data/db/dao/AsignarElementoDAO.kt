package com.example.composetest.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.composetest.Logger
import com.example.composetest.data.db.dbo.ElementoCartaDBO
import com.example.composetest.data.db.dbo.ElementoPistaDBO
import com.example.composetest.data.db.dbo.ElementoTableroDBO
import com.example.composetest.data.db.dbo.JugadorDBO
import com.example.composetest.data.db.exception.AsignarDineroException.CartaNoCambiadaDePoseedor
import com.example.composetest.data.db.exception.AsignarDineroException.CartaNoGastada
import com.example.composetest.data.db.exception.AsignarDineroException.DineroNoSumado
import com.example.composetest.data.db.exception.AsignarDineroException.JugadorNoObtenido
import com.example.composetest.data.db.exception.AsignarPistaException.PistaNoCambiadaDePoseedor
import com.example.composetest.data.db.exception.BaseDatosException
import com.example.composetest.data.db.exception.ComprarException
import com.example.composetest.data.db.exception.ComprarException.CompradoresActualizadosIncorrectos
import com.example.composetest.data.db.exception.ComprarException.DineroNegativo
import com.example.composetest.data.db.reduced.ActualizacionJugador
import com.example.composetest.data.db.reduced.AsignacionElemento
import com.example.composetest.data.db.reduced.CartaGastada
import com.example.composetest.data.mapper.fromdbo.eliminarSecreto
import com.example.composetest.data.mapper.fromdbo.nuevoSecretoAdquirido
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Dao
interface AsignarElementoDAO {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun guardarCarta(carta: ElementoCartaDBO): Long

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun guardarPista(carta: ElementoPistaDBO): Long

  @Update(onConflict = OnConflictStrategy.IGNORE, entity = ElementoCartaDBO::class)
  suspend fun actualizarCarta(carta: AsignacionElemento): Int

  @Update(onConflict = OnConflictStrategy.IGNORE, entity = ElementoPistaDBO::class)
  suspend fun actualizarPista(pista: AsignacionElemento): Int

  @Query("SELECT * FROM JugadorDBO WHERE JugadorDBO.nombre = :nombreJugador AND JugadorDBO.partida = :partida")
  fun obtenerJugador(nombreJugador: String, partida: Long): JugadorDBO?

  @Query("SELECT * FROM JugadorDBO WHERE JugadorDBO.idJugador = :idJugador")
  fun obtenerJugador(idJugador: Long): JugadorDBO?

  @Query(
    "SELECT * FROM ElementoPistaDBO " +
        "WHERE ElementoPistaDBO.idElemento = :idPista " +
        "AND ElementoPistaDBO.partida = :idPartida"
  )
  suspend fun obtenerPista(idPista: String, idPartida: Long): ElementoTableroDBO?

  @Update(entity = JugadorDBO::class)
  suspend fun actualizarJugador(jugador: JugadorDBO): Int

  private suspend fun actualizarUnJugador(jugador: JugadorDBO): Int {
    try {
      val actualizados = actualizarJugador(jugador)

      if (actualizados != 1) {
        throw JugadorNoActualizado(jugador, actualizados, null)
      } else{
        return actualizados
      }
    } catch (t: Throwable) {
      throw JugadorNoActualizado(jugador, null, t)
    }
  }

  @Transaction
  suspend fun actualizarJugador(
    jugador: ActualizacionJugador,
    onJugadorEncontrado: (JugadorDBO) -> JugadorDBO = { it }
  ): Int {
    val jugadorEncontrado = buscarJugador(jugador.nombre, jugador.partida)
    val jugadorModificado = onJugadorEncontrado(jugadorEncontrado)
    Logger.logSql("Actualizar un jugador para cambiar su cantidad de dinero")
    return actualizarUnJugador(jugadorModificado)
  }

  @Transaction
  suspend fun asignarPistaConDinero(
    asignacion: AsignacionElemento,
    monedas: Int,
    jugador: JugadorDBO,
    idPartida: Long,
  ) {
    coroutineScope {
      // Paso null porque ya no se guarda cuando se asigna, sino cuando se va a producir la visita
      launch { actualizarDineroYSecretosGuardados(jugador, monedas, null) }
      launch { asignacion.copy(monedas = 0).also { actualizarUnaPista(it) } }
    }
  }

  suspend fun asignarPistaSinDinero(actualizacion: AsignacionElemento, nuevoPoseedor: JugadorDBO) {
    coroutineScope {
      // Comentado porque ya no se guarda el secreto cuando se asigna, sino cuando se va a producir la visita
//      launch { actualizacion.idSecretoParaGuardar?.let { darSecretoAJugador(it, nuevoPoseedor) } }
      launch { actualizarUnaPista(actualizacion) }
    }
  }

  suspend fun AsignarElementoDAO.darSecretoAJugador(idSecretoNuevo: String, nuevoPoseedor: JugadorDBO) {
    val jugadorActualizado = nuevoPoseedor.nuevoSecretoAdquirido(idSecretoNuevo)
    actualizarJugador(jugadorActualizado)
  }

  suspend fun borrarSecretoSiProcede(asignacion: AsignacionElemento, idPartida: Long, idAnteriorPoseedor: Long) {
    if (asignacion.esDesasignacion && asignacion.idSecretoParaGuardar != null) {
      val antiguoPoseedor = obtenerJugadorPorId(idAnteriorPoseedor)

      if (antiguoPoseedor != null) {
        actualizarUnJugador(antiguoPoseedor.eliminarSecreto(asignacion.idSecretoParaGuardar))
      }
      // TODO Melero: 19/4/25 De momento hay forma de saber si la pista que se está reasignando
      //  tenía valor o no. Podría ser reasignada justo después de haberla asignado erróneamente
      //  o haber sido reasignada después de un robo, en cuyo caso, el dinero no procede cambiarlo.
    }
  }

  fun obtenerJugadorPorId(idJugador: Long?): JugadorDBO? =
    if (idJugador != null) {
      Logger.logSql("Obtener un jugador por id")
      obtenerJugador(idJugador) ?: throw JugadorNoEncontrado(idJugador)
    } else {
      null
    }

  private suspend fun obtenerSecreto(idSecretoParaGuardar: String, idPartida: Long): ElementoTableroDBO {
      Logger.logSql("Obtener una pista")
    val pista = obtenerPista(idSecretoParaGuardar, idPartida)

    if (pista != null) {
      return pista
    } else {
      throw SecretoNoEncontrado(idSecretoParaGuardar, idPartida)
    }
  }

  @Transaction
  suspend fun asignarDinero(
    asignacion: AsignacionElemento,
    monedas: Int,
    jugador: JugadorDBO,
    partida: Long
  ) {
    coroutineScope {
      // Paso null porque dinero no es un secreto, pero aunque lo fuera, ya no se guarda cuando se
      // asigna, sino cuando se va a producir la visita
      async { actualizarDineroYSecretosGuardados(jugador, monedas, null) }
      async { actualizarUnaCarta(asignacion) }
    }
  }

  suspend fun actualizarUnaPista(pista: AsignacionElemento) {
    Logger.logSql("Actualizar pista")
    val actualizados = actualizarPista(pista)

    if (actualizados != 1) {
      throw PistaNoCambiadaDePoseedor(pista, actualizados)
    }
  }

  suspend fun actualizarUnaCarta(carta: AsignacionElemento) {
    Logger.logSql("Actualizar una carta")
    val actualizados = actualizarCarta(carta)

    if (actualizados != 1) {
      throw CartaNoCambiadaDePoseedor(carta, actualizados)
    }
  }

  fun buscarJugador(nombreJugador: String, idPartida: Long): JugadorDBO = try {
    Logger.logSql("Obtener jugador por nombre e idPartida")
    obtenerJugador(nombreJugador, idPartida)
      ?: throw JugadorNoObtenido(nombreJugador, idPartida, null)
  } catch (t: Throwable) {
    throw JugadorNoObtenido(nombreJugador, idPartida, t)
  }

  private suspend fun actualizarDineroYSecretosGuardados(
    jugador: JugadorDBO,
    dinero: Int,
    idSecretoParaGuardar: String?
  ) {
    val dineroSumado = jugador.copy(dinero = jugador.dinero + dinero)
    val jugadorActualizado = idSecretoParaGuardar?.let { dineroSumado.nuevoSecretoAdquirido(it) } ?: dineroSumado

    try {
      val actualizados = actualizarJugador(jugadorActualizado)

      if (actualizados != 1) {
        DineroNoSumado(actualizados, jugadorActualizado, null)
      }
    } catch (t: Throwable) {
      DineroNoSumado(null, jugadorActualizado, t)
    }
  }

  /*@Query("DELETE FROM ElementoCartaDBO " +
          "WHERE ElementoCartaDBO.id = :idCarta AND ElementoCartaDBO.partida = :partida")*/

  @Update(entity = ElementoCartaDBO::class)
  suspend fun marcarComoGastada(cartaGastada: CartaGastada): Int

  suspend fun gastar(idCarta: String, idPartida: Long) {
    try {
      Logger.logSql("Marcar carta como gastada")
      val borrados = marcarComoGastada(CartaGastada(idCarta, idPartida, true))

      if (borrados != 1) {
        CartaNoGastada(idCarta, idPartida, borrados, null)
      }
    } catch (t: Throwable) {
      CartaNoGastada(idCarta, idPartida, null, t)
    }
  }

  @Transaction
  suspend fun comprar(pistaActualizada: AsignacionElemento, compradorActualizado: ActualizacionJugador) {
    restarDinero(compradorActualizado)
    actualizarUnaPista(pistaActualizada)
  }

  private suspend fun restarDinero(compradorActualizado: ActualizacionJugador) {
    if (compradorActualizado.dinero < 0) {
      throw DineroNegativo(compradorActualizado)

    } else {
      val actualizados = try {
        actualizarJugador(compradorActualizado) {
          it.copy(dinero = compradorActualizado.dinero)
        }
      } catch (t: Throwable) {
        throw ComprarException.CompradorNoActualizado(compradorActualizado, t)
      }

      if (actualizados != 1) {
        throw CompradoresActualizadosIncorrectos(actualizados)
      }
    }
  }

  @Transaction
  suspend fun robarCarta(
    brandy: ElementoCartaDBO,
    elementoRobado: AsignacionElemento,
    ladron: JugadorDBO,
    victima: ActualizacionJugador,
    idPartida: Long
  ) {
    gastar(brandy.idElemento, idPartida)
    elementoRobado.monedas?.let {
      asignarDinero(elementoRobado, it, ladron, idPartida)
//            sumarDinero(ladron.nombre, idPartida, it)
      restarDinero(victima)
    } ?: actualizarUnaCarta(elementoRobado)
  }

  @Transaction
  suspend fun robarPista(
    brandy: ElementoCartaDBO,
    elementoRobado: AsignacionElemento,
    idPartida: Long
  ) {
    gastar(brandy.idElemento, idPartida)
    actualizarUnaPista(elementoRobado)
  }

  class SecretoNoEncontrado(idSecreto: String, idPartida: Long) : BaseDatosException(
    mensaje = "No se ha encontrado el secreto $idSecreto en la partida con id $idPartida",
    cause = null
  )

  class JugadorNoEncontrado(idJugador: Long) : BaseDatosException(
    mensaje = "No se ha encontrado al jugador con id $idJugador",
    cause = null
  )

  class JugadorNoActualizado(
    jugador: JugadorDBO,
    cantidadActualizados: Int?,
    cause: Throwable?
  ): BaseDatosException(
    mensaje = cantidadActualizados?.let {
      "Se ha intentando actualizado al jugador ${jugador.nombre}, pero se han actualizado $cantidadActualizados registros"
    } ?: "Se ha producido un error al intentar actualizar al jugador",
    cause = cause
  )
}