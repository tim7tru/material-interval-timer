package com.timmytruong.materialintervaltimer.di

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.model.IntervalSound
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideSounds(): List<IntervalSound> = listOf(
        IntervalSound(-1, "None", true),
        IntervalSound(R.raw.beep, "Beep", false),
        IntervalSound(R.raw.another_beep, "Another beep", false),
        IntervalSound(R.raw.censor, "Censor", false),
        IntervalSound(R.raw.ding, "Ding", false),
        IntervalSound(R.raw.elevator, "Elevator", false),
        IntervalSound(R.raw.error, "Error", false),
        IntervalSound(R.raw.robot, "Robot", false),
        IntervalSound(R.raw.synth, "Synth", false),
    )
}

@Qualifier annotation class Recents
@Qualifier annotation class Favourites