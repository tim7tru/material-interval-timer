package com.timmytruong.materialintervaltimer.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.events.EMPTY_ERROR
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

internal const val TIMER = "timer"

@ActivityRetainedScoped
class TimerListViewModel @Inject constructor(
    private val timerRepository: TimerRepository
) : BaseViewModel() {

    private var currentTimer: Timer = Timer()
        set(value) = setEvent(TIMER, value)

    private val _recents = MutableLiveData<List<Timer>>()
    val recents: LiveData<List<Timer>> get() = _recents

    private val _favourites = MutableLiveData<List<Timer>>()
    val favourites: LiveData<List<Timer>> get() = _favourites

    fun fetchFavourites() = viewModelScope.launch {
        timerRepository.getFavouriteTimers().collectLatest(_favourites::setValue)
    }

    fun fetchRecents() = viewModelScope.launch {
        timerRepository.getRecentTimers().collectLatest(_recents::setValue)
    }

    fun onEmptyList() = setEvent(EMPTY_ERROR)

    fun onTimerClicked(timer: Timer) {
        currentTimer = timer
    }
}