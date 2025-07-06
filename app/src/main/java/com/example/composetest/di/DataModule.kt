package com.example.composetest.di

import android.content.Context
import androidx.room.Room
import com.example.composetest.Logger
import com.example.composetest.data.db.AdelaidaDatabase
import com.example.composetest.data.db.CallbackCargaInicial
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    companion object {

        var database: AdelaidaDatabase? = null

        @Singleton
        @Provides
        fun provideDataBase(@ApplicationContext context: Context, initialPopulationLatch: CountDownLatch): AdelaidaDatabase =
            Room.databaseBuilder(context, AdelaidaDatabase::class.java, "Adelaida")
                .addCallback(CallbackCargaInicial(initialPopulationLatch, context))
                .fallbackToDestructiveMigration()
                .setQueryCallback({ sqlQuery, bindArgs ->
                    Logger.logSqlQuery(formatearConsulta(sqlQuery, bindArgs))
                },
                    Executors.newSingleThreadExecutor()
                )
                .build()
                .also { database = it }

        @Singleton
        @Provides
        fun provideInitialDataLoadingLatch(): CountDownLatch = CountDownLatch(1)

        private fun formatearConsulta(
            sqlQuery: String,
            bindArgs: List<Any?>
        ): String {
            var replacementIndex = 0

            return sqlQuery.fold("") { acc, character ->
                val siguiente = if (character == '?') {
                    replacementIndex++
                    bindArgs.getOrNull(replacementIndex).toString()
                } else {
                    character.toString()
                }

                acc + siguiente
            }
        }
    }
}