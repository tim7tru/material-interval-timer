package com.timmytruong.materialintervaltimer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.timmytruong.materialintervaltimer.utils.converters.IntervalConverter
import com.timmytruong.materialintervaltimer.utils.converters.IntervalSoundConverter
import com.timmytruong.materialintervaltimer.utils.constants.AppConstants
import com.timmytruong.materialintervaltimer.utils.constants.RoomConstants

@Entity(tableName = RoomConstants.TIMER_TABLE_NAME)
data class Timer(
    @ColumnInfo
    var timer_title: String = "",

    @ColumnInfo
    var timer_created_date: String = "",

    @ColumnInfo
    var timer_updated_date: String = "",

    @ColumnInfo
    var timer_saved: Boolean = false,

    @ColumnInfo
    var timer_repeat: Boolean = false,

    @ColumnInfo
    @TypeConverters(IntervalConverter::class)
    var timer_intervals: ArrayList<Interval> = arrayListOf(),

    @ColumnInfo
    var timer_intervals_count: String = "",

    @ColumnInfo
    var timer_total_time_secs: String = "",

    @ColumnInfo
    @TypeConverters(IntervalSoundConverter::class)
    var timer_interval_sound: IntervalSound = AppConstants.SOUNDS[0]
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}