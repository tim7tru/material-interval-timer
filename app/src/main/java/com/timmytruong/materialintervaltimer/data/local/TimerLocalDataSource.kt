package com.timmytruong.materialintervaltimer.data.local

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.room.dao.TimerDao
import com.timmytruong.materialintervaltimer.data.model.Timer
import com.timmytruong.materialintervaltimer.utils.providers.DateProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TimerLocalDataSource(
    private val timerDao: TimerDao,
    private val ioDispatcher: CoroutineDispatcher,
    private val date: DateProvider
) : TimerRepository {
    override suspend fun saveNewTimer(timer: Timer): Long = withContext(ioDispatcher) {
        return@withContext timerDao.insert(timer)
    }

    override suspend fun getTimerById(id: Int): Timer = withContext(ioDispatcher) {
        return@withContext timerDao.getTimerById(id = id)
    }

    override suspend fun setFavorite(id: Int, favorite: Boolean) = withContext(ioDispatcher) {
        timerDao.setFavorite(id = id, isFavorite = favorite, date = date.getCurrentDate())
    }

    override suspend fun deleteTimer(id: Int) = withContext(ioDispatcher) {
        val timer = getTimerById(id = id)
        timerDao.deleteTimer(timer = timer)
    }

    override fun getRecentTimers(): Flow<List<Timer>> = timerDao.getRecentTimers()

    override fun getFavoritedTimers(): Flow<List<Timer>> = timerDao.getFavoritedTimers()
}