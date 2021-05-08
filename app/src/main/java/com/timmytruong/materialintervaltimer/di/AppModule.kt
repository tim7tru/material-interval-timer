package com.timmytruong.materialintervaltimer.di

import android.content.Context
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.utils.providers.AppResourceProvider
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideSounds(
        resources: ResourceProvider,
        emptySound: IntervalSound
    ): List<IntervalSound> = listOf(
        emptySound,
        IntervalSound(R.raw.beep, resources.string(R.string.beep), false),
        IntervalSound(R.raw.another_beep, resources.string(R.string.another_beep), false),
        IntervalSound(R.raw.censor, resources.string(R.string.censor), false),
        IntervalSound(R.raw.ding, resources.string(R.string.ding), false),
        IntervalSound(R.raw.elevator, resources.string(R.string.elevator), false),
        IntervalSound(R.raw.error, resources.string(R.string.error), false),
        IntervalSound(R.raw.robot, resources.string(R.string.robot), false),
        IntervalSound(R.raw.synth, resources.string(R.string.synth), false),
    )

    @Singleton
    @Provides
    fun provideEmptySound(resources: ResourceProvider) = IntervalSound(
        -1,
        resources.string(R.string.none),
        true
    )

    @Singleton
    @Provides
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider = AppResourceProvider(context)
}

@Qualifier
annotation class Recents

@Qualifier
annotation class Favourites