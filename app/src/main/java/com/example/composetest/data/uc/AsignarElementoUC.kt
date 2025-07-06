package com.example.composetest.data.uc

import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.exception.AsignarDineroException
import com.example.composetest.data.mapper.fromdbo.getIdSecretoParaGuardarSiAplica
import com.example.composetest.data.mapper.fromdbo.toDBO
import com.example.composetest.data.mapper.todbo.cambiarPoseedor
import com.example.composetest.data.uc.UC.Respuesta.Valor
import com.example.composetest.extensions.noneNull
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.ElementoTablero.Carta
import com.example.composetest.model.ElementoTablero.Pista
import javax.inject.Inject

class AsignarElementoUC @Inject constructor(
  private val baseDatos: AdelaidaDatabase,
) : UC<AsignarElementoUC.Parametros, UC.Respuesta<Boolean>>() {

  /**
   * @property elemento Lo que se quiere asignar a alguien.
   * @property nombreJugador Si es nulo, el elemento se devolverá al tablero.
   * @property partida La partida del jugador. Se podría sacar del jugador, pero si se quiere
   * devolver al tablero, sin jugador no se podría obtener la partida.
   * @property esDesasignacion Si es desasignación, se entiende que se está deshaciendo una
   * asignación errónea, por lo que si la pista era un secrto, se le tiene que quitar de la lista de
   * secretos conocidos en la ronda.
   * @param existia Solo para depurar. Si el elemento no existe, se guardará en la base de datos
   * cuando se le asigne al jugador en lugar de fallar la operación.
   */
  class Parametros(
    val elemento: ElementoTablero,
    val nombreJugador: String?,
    val partida: Long,
    val esDesasignacion: Boolean,
    val existia: Boolean,
  ) : UC.Parametros

  override suspend fun execute(parametros: Parametros): Respuesta<Boolean> = with(parametros) {
    try {
      when {
        !existia -> crearElemento()
        elemento is Pista -> asignarPista(elemento, nombreJugador, partida)
        elemento is Carta -> asignarCarta(elemento, nombreJugador, partida)
      }
      Valor(true)
    } catch (e: AsignarDineroException) {
      crearError<Boolean>(e)
    }
  }

  private suspend fun Parametros.crearElemento(): Boolean {
    return with(baseDatos.asignarElementoDao()) {
      val jugador = nombreJugador?.let {
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
    nombreJugador: String?,
    idPartida: Long,
  ) {
    with(baseDatos.asignarElementoDao()) {
      val jugador = nombreJugador?.let {
        Logger.logSql("Obtener jugador por nombre")
        baseDatos.asignarElementoDao().buscarJugador(nombreJugador, idPartida)
      }
      val idSecretoParaGuardar = jugador?.getIdSecretoParaGuardarSiAplica(elemento)
      val actualizacion = elemento.cambiarPoseedor(jugador?.id, partida, idSecretoParaGuardar,
        esDesasignacion)

      noneNull(elemento.monedas.takeIf { it > 0 }, jugador) { monedas, jugador ->
        asignarPistaConDinero(actualizacion, monedas, jugador, idPartida)
      } ?: run { actualizarUnaPista(actualizacion) }
    }
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
}