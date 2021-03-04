package com.timmytruong.materialintervaltimer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.NAVIGATE
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.home.fragments.HomeFragmentDirections
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class HomeViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val homeToCreate: HomeFragmentDirections.ActionHomeFragmentToCreateTimerFragment,
    private val homeToBottomSheet: HomeFragmentDirections.ActionHomeFragmentToTimerActionBottomSheet
) : BaseViewModel() {

    private var currentTimer: Timer = Timer()
        set(value) {
            homeToBottomSheet.apply {
                isFavourited = value.timer_saved
                timerId = value.id
            }
            setEvent(NAVIGATE, homeToBottomSheet)
            field = value
        }

    private val _favouriteTimers = MutableLiveData<List<Timer>>()
    val favouriteTimers: LiveData<List<Timer>> get() = _favouriteTimers

    private val _recentTimers = MutableLiveData<List<Timer>>()
    val recentTimers: LiveData<List<Timer>> get() = _recentTimers

    fun onTimerCardClicked(timer: Timer) {
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

    fun onAddClicked() = setEvent(NAVIGATE, homeToCreate)
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class HomeViewModelModule {

    @Provides
    fun provideNavToBottomSheet() = HomeFragmentDirections.actionHomeFragmentToTimerActionBottomSheet()

    @Provides
    fun provideNavToCreateTimer() = HomeFragmentDirections.actionHomeFragmentToCreateTimerFragment().apply {
            clearViewModel = true
        }
}