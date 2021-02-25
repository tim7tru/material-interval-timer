package com.timmytruong.materialintervaltimer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.Timer
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

internal const val RECENTS = "recents"
internal const val FAVOURITES = "favourites"
internal const val TIMER = "timer"

@ActivityRetainedScoped
class HomeViewModel @Inject constructor(
    private val timerRepository: TimerRepository
) : BaseViewModel() {

    private var currentTimer: Timer = Timer()
        set(value) = setEvent(TIMER, value)

    private val _favouriteTimers = MutableLiveData<List<Timer>>()
    val favouriteTimers: LiveData<List<Timer>> get() = _favouriteTimers

    private val _recentTimers = MutableLiveData<List<Timer>>()
    val recentTimers: LiveData<List<Timer>> get() = _recentTimers

    fun timerCardClicked(timer: Timer) {
        currentTimer = timer
    }

    fun getRecentTimers() {
        viewModelScope.launch {
            timerRepository.getRecentTimers().collect {
                when {
                    it.size < 7 -> _recentTimers.value = it.subList(0, it.size)
                    else -> _recentTimers.value = it.subList(0, 7)
                }
            }
        }
    }

    fun getFavouriteTimers() {
        viewModelScope.launch {
            timerRepository.getFavouriteTimers().collect {
                when {
                    it.size < 7 -> _favouriteTimers.value = it.subList(0, it.size)
                    else -> _favouriteTimers.value = it.subList(0, 7)
                }
            }
        }
    }

    fun onFavouritedClicked(id: Int, isFavourited: Boolean) {
        viewModelScope.launch {
            timerRepository.setShouldSave(id = id, saved = isFavourited)
        }
    }

    fun onDeleteClicked(id: Int) {
        viewModelScope.launch {
            timerRepository.deleteTimer(id = id)
        }
    }
}