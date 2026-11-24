package com.msmikeescom.minesweeper.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.msmikeescom.minesweeper.repository.local.database.MinesWeeperDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * App-wide dependencies that can't be built with an @Inject constructor. Everything else
 * (MainRepository, MinesWeeperSharedPreferences, RealTimeDatabase) is constructor-injected.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MinesWeeperDatabase =
        MinesWeeperDatabase.build(context)

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth
}
