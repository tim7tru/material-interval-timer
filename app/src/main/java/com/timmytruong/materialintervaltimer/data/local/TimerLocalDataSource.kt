package com.timmytruong.materialintervaltimer.data.local

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.room.dao.TimerDAO
import com.timmytruong.materialintervaltimer.model.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TimerLocalDataSource @Inject constructor(
    private val timerDAO: TimerDAO
): TimerRepository {
    override suspend fun saveNewTimer(timer: Timer): Long = withContext(Dispatchers.Default) {
        return@withContext timerDAO.insert(timer)
    }

    override suspend fun getTimerById(id: Int): Timer = withContext(Dispatchers.Default) {
        return@withContext timerDAO.getTimerById(id = id)
    }

    override suspend fun setShouldSave(id: Int, save: Boolean) = withContext(Dispatchers.Default) {
        timerDAO.setSaveTimer(id = id, shouldSave = save)
        return@withContext
    }
}