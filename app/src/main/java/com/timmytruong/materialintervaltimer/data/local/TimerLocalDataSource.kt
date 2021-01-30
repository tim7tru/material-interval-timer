package com.timmytruong.materialintervaltimer.data.local

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.room.dao.TimerDAO
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TimerLocalDataSource @Inject constructor(
    private val timerDao: TimerDAO
): TimerRepository {
    override suspend fun saveNewTimer(timer: Timer): Long = withContext(Dispatchers.IO) {
        return@withContext timerDao.insert(timer)
    }

    override suspend fun getTimerById(id: Int): Timer = withContext(Dispatchers.IO) {
        return@withContext timerDao.getTimerById(id = id)
    }

    override suspend fun setShouldSave(id: Int, saved: Boolean) {
        timerDao.setSaveTimer(id = id, shouldSave = saved)
        setUpdatedDate(id = id)
    }

    override suspend fun deleteTimer(id: Int) {
        val timer = getTimerById(id = id)
        timerDao.deleteTimer(timer = timer)
    }

    override fun getRecentTimers(): Flow<List<Timer>> {
        return timerDao.getRecentTimers()
    }

    override fun getFavouriteTimers(): Flow<List<Timer>> {
        return timerDao.getSavedTimers()
    }

    private suspend fun setUpdatedDate(id: Int) = withContext(Dispatchers.IO) {
        timerDao.setUpdatedDate(id = id, date = DesignUtils.getCurrentDate())
    }
}