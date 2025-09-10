package com.example.composetest

import com.example.composetest.model.ElementoTablero
import com.example.composetest.ui.compose.sampledata.jugadores
import com.example.composetest.ui.manager.Validacion
import com.example.composetest.ui.manager.ValidacionVisita
import com.example.composetest.ui.manager.run
import com.example.composetest.ui.manager.puedeSerVisitado
import junit.framework.TestCase
import org.junit.Test

class VisitaAdelaidaTest {

  @Test
  fun `Cuando un jugador cumple los requisitos para ser visitado, puede ser visitado`() {
    val jugador = jugadores("Pedrito")[0]

    // Tener al menos dos cartas y no tener el Perseskud
    jugador.desecharCartas()
    jugador.darCarta(ElementoTablero.Carta.Dinero(1, 200))
    jugador.darCarta(ElementoTablero.Carta.Dinero(2, 400))

    // Tener un secreto por el que aún no haya sido visitado
    val secreto = ElementoTablero.Pista.Secreto(1)
    jugador.desecharPistas()
    jugador.darPista(secreto)
    val jugadorConSecreto = jugador.copy(
      idsSecretosReveladosRonda = listOf(secreto.id),
      idsSecretosRevelados = emptyList()
    )
    val result = puedeSerVisitado(jugadorConSecreto)
    TestCase.assertTrue(result.run())
    assertCorrectValidations(result, true, ValidacionVisita.TieneSuficientesCartas(jugador), true)
    assertCorrectValidations(result, true, ValidacionVisita.TieneUnSecretoNuevo(jugador), true)
    assertCorrectValidations(result, true, ValidacionVisita.NoTieneElPerseskud(jugador), true)
  }

  @Test
  fun `Cuando un jugador no tiene suficientes cartas para ser visitado, no puede ser visitado`() {
    val jugador = jugadores("Pedrito")[0]
    jugador.desecharCartas()
    val result = puedeSerVisitado(jugador)
    assertCorrectValidations(result, false, ValidacionVisita.TieneSuficientesCartas(jugador), false)
  }

  private fun assertCorrectValidations(
    result: List<Validacion>,
    expectedValido: Boolean,
    expectedValidacion: ValidacionVisita,
    expectedValidacionValido: Boolean
  ) {
    val validacionResultante = result.firstOrNull { it::class == expectedValidacion::class }
    TestCase.assertEquals(expectedValido, result.run())
    TestCase.assertTrue("No tiene la validación correcta", validacionResultante != null)
    TestCase.assertEquals(
      "Contiene la validación, pero es ${!expectedValidacionValido}",
      expectedValidacionValido,
      validacionResultante!!.validar()
    )
  }

  @Test
  fun `Cuando un jugador tiene el Perseskud, no puede ser visitado`() {
    val jugador = jugadores("Pedrito")[0]
    jugador.desecharCartas()
    jugador.darCarta(ElementoTablero.Carta.Perseskud())
    val result = puedeSerVisitado(jugador)
    assertCorrectValidations(result, false, ValidacionVisita.NoTieneElPerseskud(jugador), false)
  }

  @Test
  fun `Cuando un jugador no tiene secretos en la vitrina, no puede ser visitado`() {
    val jugador = jugadores("Pedrito")[0]
    jugador.desecharPistas()
    val result = puedeSerVisitado(jugador)
    assertCorrectValidations(result, false, ValidacionVisita.TieneUnSecretoNuevo(jugador), false)
  }

  @Test
  fun `Cuando un jugador tiene secretos en la vitrina pero ha sido visitado por todos ellos, no puede ser visitado`() {
    val jugador = jugadores("Pedrito")[0]
    jugador.desecharPistas()
    val secreto1 = ElementoTablero.Pista.Secreto(1)
    val secreto2 = ElementoTablero.Pista.Secreto(2)
    val secreto3 = ElementoTablero.Pista.Secreto(3)
    jugador.darPista(secreto1)
    jugador.darPista(secreto2)
    jugador.darPista(secreto3)
    val jugadorConSecretos = jugador.copy(
      idsSecretosReveladosRonda = listOf(secreto1.id, secreto2.id, secreto3.id),
      idsSecretosRevelados = listOf(secreto1.id, secreto2.id, secreto3.id)
    )
    val result = puedeSerVisitado(jugadorConSecretos)
    assertCorrectValidations(result, false, ValidacionVisita.TieneUnSecretoNuevo(jugador), false)
  }

  @Test
  fun `Cuando un jugador tiene varios secretos pero aun no ha sido visitado por uno de ellos, la validacion no devolvera que no tiene secretos nuevos`() {
    val jugador = jugadores("Pedrito")[0]
    jugador.desecharPistas()
    val secreto1 = ElementoTablero.Pista.Secreto(1)
    val secreto2 = ElementoTablero.Pista.Secreto(2)
    jugador.darPista(secreto1)
    jugador.darPista(secreto2)
    val jugadorConSecretos = jugador.copy(
      idsSecretosReveladosRonda = listOf(secreto1.id, secreto2.id),
      idsSecretosRevelados = listOf(secreto1.id)
    )
    val result = puedeSerVisitado(jugadorConSecretos)
    assertCorrectValidations(result, false, ValidacionVisita.TieneUnSecretoNuevo(jugador), true)
  }
}