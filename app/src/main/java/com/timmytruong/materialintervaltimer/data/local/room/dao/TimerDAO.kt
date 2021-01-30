package com.timmytruong.materialintervaltimer.data.local.room.dao

import androidx.room.*
import com.timmytruong.materialintervaltimer.model.Timer
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: Timer): Long

    @Query("SELECT * FROM Timers WHERE id = :id")
    suspend fun getTimerById(id: Int): Timer

    @Query("UPDATE Timers SET timer_saved = :shouldSave WHERE id = :id")
    suspend fun setSaveTimer(id: Int, shouldSave: Boolean)

    @Query("UPDATE Timers SET timer_updated_date = :date WHERE id = :id")
    suspend fun setUpdatedDate(id: Int, date: String)

    @Query("SELECT * FROM Timers WHERE timer_saved = 1")
    fun getSavedTimers(): Flow<List<Timer>>

    @Query("SELECT * FROM Timers ORDER BY timer_updated_date DESC")
    fun getRecentTimers(): Flow<List<Timer>>

    @Delete
    suspend fun deleteTimer(timer: Timer)
}