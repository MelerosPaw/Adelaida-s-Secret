package com.example.composetest.model

import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TableroTest {

  @Test
  fun `Todos los elementos del tablero menos las 5 pistas a retirar suman 64 casillas`() {
    Assert.assertEquals(64, elementosJuego.size - 5)
  }
}