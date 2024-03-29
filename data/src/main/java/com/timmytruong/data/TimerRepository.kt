package com.timmytruong.data

import com.timmytruong.materialintervaltimer.data.model.Timer
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    suspend fun saveNewTimer(timer: Timer): Long

    suspend fun getTimerById(id: Int): Timer

    suspend fun setFavorite(id: Int, favorite: Boolean, currentDate: String)

    suspend fun deleteTimer(id: Int)

    fun getRecentTimers(): Flow<List<Timer>>

    fun getFavoritedTimers(): Flow<List<Timer>>

    fun getPresetTimers(): Flow<List<Timer>>
}