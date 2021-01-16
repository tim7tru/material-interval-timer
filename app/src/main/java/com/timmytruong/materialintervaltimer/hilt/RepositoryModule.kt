package com.timmytruong.materialintervaltimer.hilt

import com.timmytruong.materialintervaltimer.data.local.room.dao.TimerDAO
import com.timmytruong.materialintervaltimer.data.local.TimerLocalDataSource
import com.timmytruong.materialintervaltimer.data.TimerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@InstallIn(ActivityRetainedComponent::class)
@Module
object RepositoryModule {
    @ActivityRetainedScoped
    @Provides
    fun provideTimerRepository(timerDAO: TimerDAO): TimerRepository = TimerLocalDataSource(timerDAO)
}