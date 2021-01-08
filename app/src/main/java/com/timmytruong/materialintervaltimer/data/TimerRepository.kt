package com.timmytruong.materialintervaltimer.data

import com.timmytruong.materialintervaltimer.data.room.dao.TimerDAO
import com.timmytruong.materialintervaltimer.model.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TimerRepository @Inject constructor(
    private val timerDAO: TimerDAO
) {
    suspend fun saveNewTimer(timer: Timer): Long = withContext(Dispatchers.Default) {
        return@withContext timerDAO.insert(timer)
    }

    suspend fun getTimerById(id: Int): Timer = withContext(Dispatchers.Default) {
        return@withContext timerDAO.getTimerById(id = id)
    }
}