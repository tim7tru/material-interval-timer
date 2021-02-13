package com.timmytruong.materialintervaltimer.ui.favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.events.Error
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

internal const val EMPTY_LIST_ERROR = "empty"

class FavouritesViewModel @Inject constructor(
    private val timerRepository: TimerRepository
): BaseViewModel() {

    private val _favourites = MutableLiveData<List<Timer>>()
    val favourites: LiveData<List<Timer>> get() = _favourites

    fun fetchFavourites() = viewModelScope.launch {
        timerRepository.getFavouriteTimers().collectLatest(_favourites::setValue)
    }

    fun onEmptyList() = setEvent(Error.QualifierError(EMPTY_LIST_ERROR))

    fun onTimerClicked(id: Int) {}
}