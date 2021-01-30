package com.timmytruong.materialintervaltimer.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.timmytruong.materialintervaltimer.data.local.room.dao.TimerDAO
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.constants.RoomConstants
import com.timmytruong.materialintervaltimer.utils.converters.IntervalConverter
import com.timmytruong.materialintervaltimer.utils.converters.IntervalSoundConverter

@Database(
    entities = [
        Timer::class
    ],
    version = RoomConstants.ROOM_VERSION,
    exportSchema = false
)
@TypeConverters(IntervalConverter::class, IntervalSoundConverter::class)
abstract class TimerDatabase : RoomDatabase() {
    abstract fun timerDAO(): TimerDAO
}