package com.timmytruong.materialintervaltimer.hilt.modules

import com.timmytruong.materialintervaltimer.data.room.dao.TimerDAO
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
    fun provideTimerRepository(timerDAO: TimerDAO) = TimerRepository(timerDAO)
}