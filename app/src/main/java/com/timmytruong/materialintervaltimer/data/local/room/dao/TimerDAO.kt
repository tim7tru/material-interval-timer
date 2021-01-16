package com.timmytruong.materialintervaltimer.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.timmytruong.materialintervaltimer.model.Timer

@Dao
interface TimerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(timer: Timer): Long

    @Query("SELECT * FROM Timers WHERE timer_saved = 1")
    fun getSavedTimers(): List<Timer>

    @Query("SELECT * FROM Timers WHERE id = :id")
    fun getTimerById(id: Int): Timer

    @Query("UPDATE Timers SET timer_saved = :shouldSave WHERE id = :id")
    fun setSaveTimer(id: Int, shouldSave: Boolean)
}