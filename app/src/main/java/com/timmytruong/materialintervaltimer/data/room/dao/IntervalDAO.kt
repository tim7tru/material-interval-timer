package com.timmytruong.materialintervaltimer.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.timmytruong.materialintervaltimer.model.Interval

@Dao
interface IntervalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(interval: Interval)
}