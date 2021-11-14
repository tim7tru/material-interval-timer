package com.timmytruong.materialintervaltimer.di.modules

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.model.IntervalSound
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    val EMPTY_SOUND = IntervalSound(-1, "None", true)

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
    fun provideEmptySound() = EMPTY_SOUND

    @Singleton
    @Provides
    fun provideIcons(): List<Int> = listOf(
        R.drawable.ic_fitness_center,
        R.drawable.ic_accessibility_24px,
        R.drawable.ic_speed_24px,
        R.drawable.ic_local_cafe_24px,
        R.drawable.ic_android_24px,
        R.drawable.ic_audiotrack_24px,
        R.drawable.ic_language_24px,
        R.drawable.ic_email_24px,
        R.drawable.ic_eco_24px,
        R.drawable.ic_call_24px,
        R.drawable.ic_play,
        R.drawable.ic_pause_24px
    )
}