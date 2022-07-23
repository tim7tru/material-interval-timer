package com.timmytruong.data.local

import com.timmytruong.data.TimerRepository
import com.timmytruong.data.local.room.dao.TimerDao
import com.timmytruong.materialintervaltimer.data.model.Timer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TimerLocalDataSource(
    private val timerDao: TimerDao,
    private val ioDispatcher: CoroutineDispatcher,
) : TimerRepository {
    override suspend fun saveNewTimer(timer: Timer): Long = withContext(ioDispatcher) {
        return@withContext timerDao.insert(timer)
    }

    override suspend fun getTimerById(id: Int): Timer = withContext(ioDispatcher) {
        return@withContext timerDao.getTimerById(id = id)
    }

    override suspend fun setFavorite(id: Int, favorite: Boolean, currentDate: String) = withContext(ioDispatcher) {
        timerDao.setFavorite(id = id, isFavorite = favorite, date = currentDate)
    }

    override suspend fun deleteTimer(id: Int) = withContext(ioDispatcher) {
        val timer = getTimerById(id = id)
        timerDao.deleteTimer(timer = timer)
    }

    override fun getRecentTimers(): Flow<List<Timer>> = timerDao.getRecentTimers()

    override fun getFavoritedTimers(): Flow<List<Timer>> = timerDao.getFavoritedTimers()
}