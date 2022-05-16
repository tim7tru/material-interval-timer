package com.timmytruong.materialintervaltimer.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.timmytruong.materialintervaltimer.data.local.room.dao.TimerDao
import com.timmytruong.materialintervaltimer.data.model.Timer
import com.timmytruong.materialintervaltimer.utils.converters.IntervalConverter
import com.timmytruong.materialintervaltimer.utils.converters.IntervalSoundConverter

@Database(
    entities = [Timer::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(IntervalConverter::class, IntervalSoundConverter::class)
abstract class TimerDatabase : RoomDatabase() {
    abstract fun timerDAO(): TimerDao
}