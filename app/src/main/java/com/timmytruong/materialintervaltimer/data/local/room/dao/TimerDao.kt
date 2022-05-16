package com.timmytruong.materialintervaltimer.data.local.room.dao

import androidx.room.*
import com.timmytruong.materialintervaltimer.data.model.Timer
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: Timer): Long

    @Query("SELECT * FROM Timers WHERE id = :id")
    suspend fun getTimerById(id: Int): Timer

    @Query("UPDATE Timers SET is_favorite = :isFavorite, updated_date = :date WHERE id = :id")
    suspend fun setFavorite(id: Int, isFavorite: Boolean, date: String)

    @Query("UPDATE Timers SET updated_date = :date WHERE id = :id")
    suspend fun setUpdatedDate(id: Int, date: String)

    @Query("SELECT * FROM Timers WHERE is_Favorite = 1")
    fun getFavoritedTimers(): Flow<List<Timer>>

    @Query("SELECT * FROM Timers ORDER BY updated_date DESC")
    fun getRecentTimers(): Flow<List<Timer>>

    @Delete
    suspend fun deleteTimer(timer: Timer)
}