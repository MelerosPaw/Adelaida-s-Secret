package com.example.composetest

import android.util.Log

private const val prefix: String = "Adelaida - "

class Logger(private val tag: String) {

    companion object {

        fun logSql(queEstasHaciendo: String) {
            Logger("SQL").log(">> $queEstasHaciendo")
        }

        fun logSqlQuery(queEstasHaciendo: String) {
            Logger("SQL").log("  $queEstasHaciendo")
        }
    }

    fun log(message: String) {
        Log.i("$prefix$tag", message)
    }

    fun error(message: String, t: Throwable? = null) {
        Log.e("$prefix$tag", message, t)
    }
}