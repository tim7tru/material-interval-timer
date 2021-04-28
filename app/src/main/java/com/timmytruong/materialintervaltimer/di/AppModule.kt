package com.timmytruong.materialintervaltimer.di

import android.content.Context
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.model.IntervalSound
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.lang.ref.WeakReference
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideSounds(emptySound: IntervalSound): List<IntervalSound> = listOf(
        emptySound,
        IntervalSound(R.raw.beep, "Beep", false),
        IntervalSound(R.raw.another_beep, "Another beep", false),
        IntervalSound(R.raw.censor, "Censor", false),
        IntervalSound(R.raw.ding, "Ding", false),
        IntervalSound(R.raw.elevator, "Elevator", false),
        IntervalSound(R.raw.error, "Error", false),
        IntervalSound(R.raw.robot, "Robot", false),
        IntervalSound(R.raw.synth, "Synth", false),
    )

    @Singleton
    @Provides
    fun provideEmptySound() = IntervalSound(-1, "None", true)

    @WeakContext
    @Singleton
    @Provides
    fun provideWeakContext(@ApplicationContext context: Context): WeakReference<Context> =
        WeakReference(context)
}

@Qualifier
annotation class WeakContext
@Qualifier
annotation class Recents
@Qualifier
annotation class Favourites