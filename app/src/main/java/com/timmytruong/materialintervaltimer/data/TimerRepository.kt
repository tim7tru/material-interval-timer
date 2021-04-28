package com.timmytruong.materialintervaltimer.data

import com.timmytruong.materialintervaltimer.model.Timer
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    suspend fun saveNewTimer(timer: Timer): Long

    suspend fun getTimerById(id: Int): Timer

    suspend fun setFavourite(id: Int, favourite: Boolean)

    suspend fun deleteTimer(id: Int)

    fun getRecentTimers(): Flow<List<Timer>>

    fun getFavouritedTimers(): Flow<List<Timer>>
}