package com.timmytruong.materialintervaltimer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.timmytruong.materialintervaltimer.utils.constants.RoomConstants

@Entity(tableName = RoomConstants.INTERVAL_TABLE_NAME)
data class Interval(
    @ColumnInfo
    var interval_name: String = "",
    @ColumnInfo
    var interval_icon_id: String = "",

    @ColumnInfo
    var interval_time_secs: String = "",

    @ColumnInfo
    var interval_time_format: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var interval_id: Int = -1
}