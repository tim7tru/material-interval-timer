package com.timmytruong.materialintervaltimer.ui.home

import com.timmytruong.data.TimerRepository
import com.timmytruong.data.di.BackgroundDispatcher
import com.timmytruong.data.di.MainDispatcher
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.reusable.item.TimerItem
import com.timmytruong.materialintervaltimer.ui.reusable.type.TimerType
import com.timmytruong.materialintervaltimer.ui.reusable.item.toTimerItems
import com.timmytruong.materialintervaltimer.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val directions: HomeDirections,
    private val timerRepository: TimerRepository
) : BaseViewModel() {

    companion object {
        private const val NUM_TIMERS_SHOWN = 7
    }

    private val _recents = MutableSharedFlow<List<TimerItem>>()
    val recents: Flow<List<TimerItem>> = _recents

    private val _favorites = MutableSharedFlow<List<TimerItem>>()
    val favorites: Flow<List<TimerItem>> = _favorites

    private val _presets = MutableSharedFlow<List<TimerItem>>()
    val presets: Flow<List<TimerItem>> = _presets

    fun fetchRecents() = startSuspending(ioDispatcher) {
        _recents.emitAll(timerRepository.getRecentTimers().toTimerItems(NUM_TIMERS_SHOWN, ::onTimerClicked))
    }

    fun fetchFavorites() = startSuspending(ioDispatcher) {
        _favorites.emitAll(timerRepository.getFavoritedTimers().toTimerItems(NUM_TIMERS_SHOWN, ::onTimerClicked))
    }

    fun fetchPresets() = startSuspending(ioDispatcher) {
        _presets.emitAll(timerRepository.getPresetTimers().toTimerItems(number = 6, ::onTimerClicked))
    }

    fun onAddClicked() = Event.Navigate(directions.toCreateTimer()).fire()

    fun onFavoritesSeeAllClicked() = Event.Navigate(directions.toTimerList(TimerType.FAVORITES)).fire()

    fun onRecentsSeeAllClicked() = Event.Navigate(directions.toTimerList(TimerType.RECENTS)).fire()

    private fun onTimerClicked(id: Int, favorited: Boolean) = Event.Navigate(directions.toBottomSheet(id, favorited)).fire()
}