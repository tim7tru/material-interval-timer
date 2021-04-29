package com.timmytruong.materialintervaltimer.di

import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressAnimation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ProgressModule {

    @HorizontalProgress
    @Singleton
    @Provides
    fun provideHorizontalProgress() = ProgressAnimation(DecelerateInterpolator())

    @CircularProgress
    @Singleton
    @Provides
    fun provideCircularProgress() = ProgressAnimation(LinearInterpolator())
}

@Qualifier annotation class HorizontalProgress
@Qualifier annotation class CircularProgress