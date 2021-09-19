package com.timmytruong.materialintervaltimer.di.modules

import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.timmytruong.materialintervaltimer.di.CircularProgress
import com.timmytruong.materialintervaltimer.di.HorizontalProgress
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressAnimation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ProgressModule {

    @HorizontalProgress
    @Singleton
    @Provides
    fun provideHorizontalProgress() = ProgressAnimation(DecelerateInterpolator())

    @CircularProgress
    @Singleton
    @Provides
    fun provideCircularProgress() = ProgressAnimation(LinearInterpolator())
}