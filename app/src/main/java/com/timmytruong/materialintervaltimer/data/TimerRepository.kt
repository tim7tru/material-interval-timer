package com.timmytruong.materialintervaltimer.data

import com.timmytruong.materialintervaltimer.model.Timer

interface TimerRepository {
    suspend fun saveNewTimer(timer: Timer): Long

    suspend fun getTimerById(id: Int): Timer

    suspend fun setShouldSave(id: Int, save: Boolean)

}