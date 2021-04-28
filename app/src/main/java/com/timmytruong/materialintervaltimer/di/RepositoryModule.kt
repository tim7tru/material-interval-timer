package com.timmytruong.materialintervaltimer.di

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.data.local.TimerLocalDataSource
import com.timmytruong.materialintervaltimer.data.local.room.dao.TimerDao
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.Timer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Qualifier

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

@Qualifier
annotation class TimerStore
@Qualifier
annotation class IntervalStore