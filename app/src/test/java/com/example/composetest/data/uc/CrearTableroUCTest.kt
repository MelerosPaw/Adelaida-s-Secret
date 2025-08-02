package com.example.composetest.data.uc

import com.example.composetest.model.Partida
import com.example.composetest.model.Rasgo
import com.example.composetest.model.Secreto
import com.example.composetest.model.Sospechoso
import com.example.composetest.model.Tablero
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CrearTableroUCTest {

  @Test
  fun `El tablero se crea con 64 elementos`() {
    runBlocking {
      val partida = mockk<Partida>()
      val rasgos = listOf(Rasgo("H1", "", ""), Rasgo("F4", "", ""), Rasgo("C3", "", "")).toTypedArray()
      val secreto = Secreto("S6", "S7", "", "")
      val asesino = Sospechoso("Alguien", rasgos, secreto, Sospechoso.Genero.MUJER)
      every { partida.asesino } returns asesino

      runBlocking {
        val respuesta: UC.Respuesta<Tablero> = CrearTableroUC()
          .execute(CrearTableroUC.Parametros(partida))
        val actual = (respuesta as? UC.Respuesta.Valor)?.valor
        TestCase.assertNotNull(actual != null)
        TestCase.assertEquals(64, actual?.casillas?.size)
      }
    }
  }
}