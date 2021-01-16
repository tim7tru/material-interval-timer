package com.timmytruong.materialintervaltimer.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.timmytruong.materialintervaltimer.model.IntervalSound

@Dao
interface IntervalSoundDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(intervalSound: IntervalSound)
}