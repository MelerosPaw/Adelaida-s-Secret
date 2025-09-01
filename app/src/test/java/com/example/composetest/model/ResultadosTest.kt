package com.example.composetest.model

import junit.framework.TestCase
import org.junit.Test

class ResultadosTest {

  @Test
  fun `Al generar resultados de dados, estos se ordenan de menor a mayor`() {
    val resultados = getDistincDieRolls(4)

    resultados.windowed(2).forEach {
      println("${it[0]} es menor que ${it[1]}")
      TestCase.assertTrue(it[0] < it[1])
    }
  }
}