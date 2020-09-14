package com.timmytruong.materialintervaltimer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.timmytruong.materialintervaltimer.utils.constants.RoomConstants

@Entity(tableName = RoomConstants.INTERVAL_SOUNDS_TABLE_NAME)
data class IntervalSound(
    @PrimaryKey
    var sound_id: Int = -1,

    @ColumnInfo
    var sound_name: String = "",

    @ColumnInfo
    var sound_is_selected: Boolean = false
)