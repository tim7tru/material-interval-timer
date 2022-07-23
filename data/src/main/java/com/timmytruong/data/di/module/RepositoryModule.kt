package com.timmytruong.data.di.module

import com.timmytruong.data.TimerRepository
import com.timmytruong.data.di.BackgroundDispatcher
import com.timmytruong.data.di.IntervalStore
import com.timmytruong.data.di.TimerStore
import com.timmytruong.data.local.Store
import com.timmytruong.data.local.TimerLocalDataSource
import com.timmytruong.data.local.room.dao.TimerDao
import com.timmytruong.data.model.Interval
import com.timmytruong.data.model.Timer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher

@InstallIn(ActivityRetainedComponent::class)
@Module
object RepositoryModule {

    @ActivityRetainedScoped
    @Provides
    @TimerStore
    fun provideTimerStore() = Store(Timer())

    @ActivityRetainedScoped
    @Provides
    @IntervalStore
    fun provideIntervalStore() = Store(Interval())

    @ActivityRetainedScoped
    @Provides
    fun provideTimerRepository(
        timerDao: TimerDao,
        @BackgroundDispatcher dispatcher: CoroutineDispatcher
    ): TimerRepository = TimerLocalDataSource(timerDao, dispatcher)
}