package com.timmytruong.materialintervaltimer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.Timer
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class HomeViewModel @Inject constructor(
    private val timerRepository: TimerRepository
): BaseViewModel() {

    private val _favouriteTimers = MutableLiveData<List<Timer>>()
    val favouriteTimers: LiveData<List<Timer>> get() = _favouriteTimers

    private val _recentTimers = MutableLiveData<List<Timer>>()
    val recentTimers: LiveData<List<Timer>> get() = _recentTimers
}