package com.timmytruong.materialintervaltimer.di.modules

import android.content.Context
import androidx.room.Room
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.data.local.room.TimerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext context: Context
    ): TimerDatabase =
        Room.databaseBuilder(context, TimerDatabase::class.java, context.getString(R.string.database_name)).build()

    @Singleton
    @Provides
    fun provideTimerDao(db: TimerDatabase) = db.timerDAO()
}