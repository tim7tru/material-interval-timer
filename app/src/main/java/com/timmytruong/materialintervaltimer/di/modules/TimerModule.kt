package com.timmytruong.materialintervaltimer.di.modules

import com.timmytruong.data.di.MainDispatcher
import com.timmytruong.materialintervaltimer.utils.IntervalTimer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher

@InstallIn(ActivityRetainedComponent::class)
@Module
object TimerModule {

    @ActivityRetainedScoped
    @Provides
    fun provideIntervalTimer(
        @MainDispatcher dispatcher: CoroutineDispatcher
    ): IntervalTimer = IntervalTimer(dispatcher)
}