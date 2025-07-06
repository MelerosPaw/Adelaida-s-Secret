package com.example.composetest.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


fun formatearFecha(fecha: Date): String {
  val fechaString = SimpleDateFormat("dd 'de' MMMM, yyyy").format(fecha)
  val horaString = SimpleDateFormat.getTimeInstance().format(fecha)
  return "$fechaString, $horaString"
}

fun formatearSoloFecha(fecha: Date): String = try {
  SimpleDateFormat("dd 'de' MMMM, yyyy").format(fecha)
} catch (e: ParseException) {
  e.printStackTrace()
  ""
}

fun procesarFecha(fecha: String): Date? = try {
  SimpleDateFormat("dd 'de' MMMM, yyyy, HH:mm:ss").parse(fecha)
} catch (e: ParseException) {
  e.printStackTrace()
  null
}

infix fun Date.esElMismoDia(otraFecha: Date) : Boolean {
  val thisCalendar = Calendar.getInstance()
  thisCalendar.timeInMillis = time

  val otherCalendar = Calendar.getInstance()
  otherCalendar.timeInMillis = otraFecha.time

  return thisCalendar.get(Calendar.DAY_OF_MONTH) == otherCalendar.get(Calendar.DAY_OF_MONTH)
}

inline fun <R, T, U> noneNull(param: T?, param2: U?, block: (T, U) -> R): R? =
  if (param != null && param2 != null) {
    block(param, param2)
  } else {
    null
  }

inline fun <R, T, U, V> noneNull(param: T?, param2: U?, param3: V?, block: (T, U, V) -> R): R? =
  if (param != null && param2 != null && param3 != null) {
    block(param, param2, param3)
  } else {
    null
  }