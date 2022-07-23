package com.timmytruong.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.timmytruong.data.local.room.dao.TimerDao
import com.timmytruong.data.util.converter.IntervalConverter
import com.timmytruong.data.util.converter.IntervalSoundConverter
import com.timmytruong.materialintervaltimer.data.model.Timer

@Database(
    entities = [Timer::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(IntervalConverter::class, IntervalSoundConverter::class)
abstract class TimerDatabase : RoomDatabase() {
    abstract fun timerDAO(): TimerDao
}