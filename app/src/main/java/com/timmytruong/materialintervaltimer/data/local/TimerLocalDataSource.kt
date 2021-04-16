package com.timmytruong.materialintervaltimer.data.local

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.room.dao.TimerDAO
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.getCurrentDate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TimerLocalDataSource(
    private val timerDao: TimerDAO,
    private val ioDispatcher: CoroutineDispatcher
) : TimerRepository {
    override suspend fun saveNewTimer(timer: Timer): Long = withContext(ioDispatcher) {
        return@withContext timerDao.insert(timer)
    }

    override suspend fun getTimerById(id: Int): Timer = withContext(ioDispatcher) {
        return@withContext timerDao.getTimerById(id = id)
    }

    override suspend fun setShouldSave(id: Int, saved: Boolean) = withContext(ioDispatcher) {
        timerDao.setSaveTimer(id = id, shouldSave = saved, date = getCurrentDate())
    }

    override suspend fun deleteTimer(id: Int) = withContext(ioDispatcher) {
        val timer = getTimerById(id = id)
        timerDao.deleteTimer(timer = timer)
    }

    override fun getRecentTimers(): Flow<List<Timer>> = timerDao.getRecentTimers()

    override fun getFavouriteTimers(): Flow<List<Timer>> = timerDao.getSavedTimers()
}