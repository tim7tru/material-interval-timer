package com.timmytruong.materialintervaltimer.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DispatchersModule {

    @Singleton
    @BackgroundDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @MainDispatcher
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}

@Qualifier annotation class BackgroundDispatcher
@Qualifier annotation class MainDispatcher