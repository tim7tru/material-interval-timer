package com.timmytruong.materialintervaltimer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.timmytruong.materialintervaltimer.utils.converters.IntervalConverter
import com.timmytruong.materialintervaltimer.utils.converters.IntervalSoundConverter

@Entity(tableName = "Timers")
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
    var timer_intervals_count: Int = 0,

    @ColumnInfo
    var timer_total_time_ms: Int = 0,

    @ColumnInfo
    @TypeConverters(IntervalSoundConverter::class)
    var timer_interval_sound: IntervalSound = IntervalSound(-1, "None", true)
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun clear() {
        timer_title = ""
        timer_created_date = ""
        timer_updated_date = ""
        timer_saved = false
        timer_repeat = false
        timer_intervals = arrayListOf()
        timer_intervals_count = 0
        timer_total_time_ms = 0
        timer_interval_sound = IntervalSound(-1)
        id = 0
    }

    fun setTotalTime() {
        var totalTimeMilli = 0
        timer_intervals.forEach {
            totalTimeMilli += it.interval_time_ms
        }
        timer_total_time_ms = totalTimeMilli
    }
}