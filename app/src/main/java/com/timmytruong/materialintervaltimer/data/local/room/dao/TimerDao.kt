package com.timmytruong.materialintervaltimer.data.local.room.dao

import androidx.room.*
import com.timmytruong.materialintervaltimer.model.Timer
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: Timer): Long

    @Query("SELECT * FROM Timers WHERE id = :id")
    suspend fun getTimerById(id: Int): Timer

    @Query("UPDATE Timers SET is_favourite = :isFavourite, updated_date = :date WHERE id = :id")
    suspend fun setFavourite(id: Int, isFavourite: Boolean, date: String)

    @Query("UPDATE Timers SET updated_date = :date WHERE id = :id")
    suspend fun setUpdatedDate(id: Int, date: String)

    @Query("SELECT * FROM Timers WHERE is_favourite = 1")
    fun getFavouritedTimers(): Flow<List<Timer>>

    @Query("SELECT * FROM Timers ORDER BY updated_date DESC")
    fun getRecentTimers(): Flow<List<Timer>>

    @Delete
    suspend fun deleteTimer(timer: Timer)
}