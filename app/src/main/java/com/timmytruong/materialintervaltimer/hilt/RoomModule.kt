package com.timmytruong.materialintervaltimer.hilt

import android.content.Context
import androidx.room.Room
import com.timmytruong.materialintervaltimer.data.room.TimerDatabase
import com.timmytruong.materialintervaltimer.utils.constants.RoomConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RoomModule {
    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): TimerDatabase =
        Room.databaseBuilder(context, TimerDatabase::class.java, RoomConstants.DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideTimerDao(db: TimerDatabase) = db.timerDAO()

    @Singleton
    @Provides
    fun provideIntervalDao(db: TimerDatabase) = db.intervalDAO()

    @Singleton
    @Provides
    fun provideIntervalSoundDao(db: TimerDatabase) = db.intervalSoundDAO()
}