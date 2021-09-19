package com.timmytruong.materialintervaltimer.di.modules

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.data.local.TimerLocalDataSource
import com.timmytruong.materialintervaltimer.data.local.room.dao.TimerDao
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.IntervalStore
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.providers.DateProvider
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
        @BackgroundDispatcher dispatcher: CoroutineDispatcher,
        date: DateProvider
    ): TimerRepository = TimerLocalDataSource(timerDao, dispatcher, date)
}