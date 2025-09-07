package com.example.composetest.data.uc

import androidx.room.withTransaction
import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.dao.AsignarElementoDAO
import com.example.composetest.data.db.dbo.JugadorDBO
import com.example.composetest.data.db.exception.AsignarDineroException
import com.example.composetest.data.db.reduced.AsignacionElemento
import com.example.composetest.data.mapper.fromdbo.getIdSecretoParaGuardarSiAplica
import com.example.composetest.data.mapper.fromdbo.toDBO
import com.example.composetest.data.mapper.todbo.cambiarPoseedor
import com.example.composetest.data.uc.TipoAsignacionPista.DevolucionAlTablero
import com.example.composetest.data.uc.TipoAsignacionPista.ReasignacionPorError
import com.example.composetest.data.uc.TipoAsignacionPista.RecogerDelTablero
import com.example.composetest.data.uc.UC.Respuesta.Valor
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.ElementoTablero.Carta
import com.example.composetest.model.ElementoTablero.Pista
import javax.inject.Inject

class AsignarElementoUC @Inject constructor(
  private val baseDatos: AdelaidaDatabase,
) : UC<AsignarElementoUC.Parametros, UC.Respuesta<Boolean>>() {

  /**
   * @property elemento Lo que se quiere asignar a alguien.
   * @property nombreNuevoPoseedor Si es nulo, el elemento se devolverá al tablero.
   * @property partida La partida del jugador. Se podría sacar del jugador, pero si se quiere
   * devolver al tablero, sin jugador no se podría obtener la partida.
   * @property esDesasignacion Si es desasignación, se entiende que se está deshaciendo una
   * asignación errónea, por lo que si la pista era un secrto, se le tiene que quitar de la lista de
   * secretos conocidos en la ronda. Solo será desasignación cuando provenga del cuadro de diálogo
   * de Asignar y la carta tenga ya un dueño. Si no lo tuviese, es que viene del tablero, por lo que
   * no es desasignación.
   * @param existia Solo para depurar. Si el elemento no existe, se guardará en la base de datos
   * cuando se le asigne al jugador en lugar de fallar la operación.
   */
  class Parametros(
    val elemento: ElementoTablero,
    val nombreNuevoPoseedor: String?,
    val partida: Long,
    val esDesasignacion: Boolean,
    val existia: Boolean,
  ) : UC.Parametros

  override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
    try {
      when {
        !existia -> crearElemento()
        elemento is Pista -> asignarPista(elemento, nombreNuevoPoseedor, partida)
        elemento is Carta -> asignarCarta(elemento, nombreNuevoPoseedor, partida)
      }
      Valor(true)
    } catch (e: AsignarDineroException) {
      crearError<Boolean>(e)
    }
  }

  private suspend fun Parametros.crearElemento(): Boolean {
    return with(baseDatos.asignarElementoDao()) {
      val jugador = nombreNuevoPoseedor?.let {
        Logger.logSql("Obtener jugador para asignarle un elemento")
        obtenerJugador(it, partida)
      }

      when (elemento) {
        is Pista -> {
          Logger.logSql("Asignar pista")
          guardarPista(elemento.toDBO(jugador?.id, partida))
        }

        is Carta -> {
          Logger.logSql("Asignar carta")
          guardarCarta(elemento.toDBO(jugador?.id, partida))
        }
      } == 1L
    }
  }

  private suspend fun Parametros.asignarPista(
    elemento: Pista,
    nombreNuevoPoseedor: String?,
    idPartida: Long,
  ) {
    with(baseDatos.asignarElementoDao()) {

      Logger.logSql("Transacción para asignar una pista")
      baseDatos.withTransaction {

        val nuevoPoseedor = nombreNuevoPoseedor?.let {
          Logger.logSql("-- Obtener jugador por nombre")
          buscarJugador(nombreNuevoPoseedor, idPartida)
        }

        Logger.logSql("-- Obtener la pista por id de pista y de partida")
        val idAnteriorPoseedor = obtenerPista(elemento.id, idPartida)?.idJugador
        val idSecretoParaGuardar = nuevoPoseedor?.getIdSecretoParaGuardarSiAplica(elemento)
        val actualizacion = elemento.cambiarPoseedor(nuevoPoseedor?.id, partida,
          idSecretoParaGuardar, esDesasignacion && idAnteriorPoseedor != null)
        val tipoAsignacion = determinarTipoAsignacion(actualizacion, elemento, idAnteriorPoseedor,
          nuevoPoseedor)

        when (tipoAsignacion) {
          is RecogerDelTablero -> recogerPistaDelTablero(tipoAsignacion, actualizacion, idPartida)
          is DevolucionAlTablero -> actualizarUnaPista(actualizacion)
          is ReasignacionPorError -> {
            // Comentado porque ya no se guarda el secreto cuando se asigna, sino cuando se va a
            // producir la visita, por lo tanto, no procede quitarle el secreto a nadie de al reasignar
//            borrarSecretoSiProcede(actualizacion, idPartida, tipoAsignacion.idAnteriorPoseedor)
            asignarPistaSinDinero(actualizacion, tipoAsignacion.nuevoPoseedor)
            actualizarUnaPista(actualizacion)
          }
        }
      }
    }
  }

  private suspend fun AsignarElementoDAO.recogerPistaDelTablero(
    tipoAsignacion: RecogerDelTablero,
    actualizacion: AsignacionElemento,
    idPartida: Long
  ) {
    tipoAsignacion.monedas?.takeIf { it > 0 }
      ?.let { asignarPistaConDinero(actualizacion, it, tipoAsignacion.nuevoPoseedor, idPartida) }
      ?: run { asignarPistaSinDinero(actualizacion, tipoAsignacion.nuevoPoseedor) }
  }

  private suspend fun Parametros.asignarCarta(
    elemento: Carta,
    nombreJugador: String?,
    idPartida: Long,
  ) {
    with(baseDatos.asignarElementoDao()) {
      val jugador = nombreJugador?.let { buscarJugador(nombreJugador, idPartida) }
      val actualizacion = elemento.cambiarPoseedor(jugador?.id, partida, null)

      if (elemento is Carta.Dinero) {
        jugador?.let { asignarDinero(actualizacion, elemento.monedas, it, idPartida) }
      } else {
        actualizarUnaCarta(actualizacion)
      }
    }
  }

  private fun determinarTipoAsignacion(
    actualizacion: AsignacionElemento,
    pista: Pista,
    idAnteriorPoseedor: Long?,
    nuevoPoseedor: JugadorDBO?
  ): TipoAsignacionPista = when {
    actualizacion.esDesasignacion && idAnteriorPoseedor != null && nuevoPoseedor != null ->
      ReasignacionPorError(idAnteriorPoseedor, nuevoPoseedor)

    pista.monedas > 0 && nuevoPoseedor != null ->
      RecogerDelTablero(pista.monedas, nuevoPoseedor)

    else ->
      DevolucionAlTablero()
  }
}

private sealed class TipoAsignacionPista {

  /** Asignación desde el tablero a un jugador. Si ya se devolvió al tablero, no llevará dinero. */
  class RecogerDelTablero(val monedas: Int?, val nuevoPoseedor: JugadorDBO): TipoAsignacionPista()

  /** Se está devolviendo al tablero, indiferentemente de si tiene dinero o no porque en ese caso, al tablero no hay que sumarle dinero. La pista lo conserva. */
  class DevolucionAlTablero: TipoAsignacionPista()

  /** Se está cambiando de manos */
  class ReasignacionPorError(val idAnteriorPoseedor: Long, val nuevoPoseedor: JugadorDBO): TipoAsignacionPista()
}