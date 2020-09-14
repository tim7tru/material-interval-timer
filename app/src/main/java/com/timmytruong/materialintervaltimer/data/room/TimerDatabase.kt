package com.timmytruong.materialintervaltimer.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.timmytruong.materialintervaltimer.data.room.dao.IntervalDAO
import com.timmytruong.materialintervaltimer.data.room.dao.IntervalSoundDAO
import com.timmytruong.materialintervaltimer.data.room.dao.TimerDAO
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.converters.IntervalConverter
import com.timmytruong.materialintervaltimer.utils.converters.IntervalSoundConverter
import com.timmytruong.materialintervaltimer.utils.constants.RoomConstants

@Database(entities = [Timer::class, Interval::class, IntervalSound::class], version = RoomConstants.ROOM_VERSION)
@TypeConverters(IntervalConverter::class, IntervalSoundConverter::class)
abstract class TimerDatabase : RoomDatabase() {
    abstract fun timerDAO(): TimerDAO

    abstract fun intervalDAO(): IntervalDAO

    abstract fun intervalSoundDAO(): IntervalSoundDAO
}