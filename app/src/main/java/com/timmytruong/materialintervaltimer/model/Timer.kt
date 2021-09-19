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
    var title: String = "",

    @ColumnInfo(name = "created_date")
    var createdDate: String = "",

    @ColumnInfo(name = "updated_date")
    var updatedDate: String = "",

    @ColumnInfo(name = "is_favourite")
    var isFavourited: Boolean = false,

    @ColumnInfo(name = "should_repeat")
    var shouldRepeat: Boolean = false,

    @ColumnInfo
    @TypeConverters(IntervalConverter::class)
    var intervals: ArrayList<Interval> = arrayListOf(),

    @ColumnInfo(name = "interval_count")
    var intervalCount: Int = 0,

    @ColumnInfo(name = "total_time_ms")
    var totalTimeMs: Long = 0L,

    @ColumnInfo(name = "interval_sound")
    @TypeConverters(IntervalSoundConverter::class)
    var intervalSound: IntervalSound = IntervalSound(-1, "None", true)
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun clear() {
        title = ""
        createdDate = ""
        updatedDate = ""
        isFavourited = false
        shouldRepeat = false
        intervals = arrayListOf()
        intervalCount = 0
        totalTimeMs = 0L
        intervalSound = IntervalSound(-1)
        id = 0
    }

    fun setTotalTime() = intervals.forEach { totalTimeMs += it.timeMs }
}