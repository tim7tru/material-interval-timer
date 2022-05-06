package com.timmytruong.materialintervaltimer.ui.home

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.list.TimerType
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.TimerItem
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.toTimerItems
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
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

    fun fetchRecents() = startSuspending(ioDispatcher) {
        _recents.emitAll(timerRepository.getRecentTimers().toTimerItems())
    }

    fun fetchFavorites() = startSuspending(ioDispatcher) {
        _favorites.emitAll(timerRepository.getFavoritedTimers().toTimerItems())
    }

    fun onAddClicked() = Event.Navigate(directions.toCreateTimer()).fire()

    fun onFavoritesSeeAllClicked() = Event.Navigate(directions.toTimerList(TimerType.FAVORITES)).fire()

    fun onRecentsSeeAllClicked() = Event.Navigate(directions.toTimerList(TimerType.RECENTS)).fire()

    private fun Flow<List<Timer>>.toTimerItems() = map { it.trim().toTimerItems(::onTimerClicked) }

    private fun onTimerClicked(id: Int, favorited: Boolean) = Event.Navigate(directions.toBottomSheet(id, favorited)).fire()

    private fun List<Timer>.trim() = when {
        size < NUM_TIMERS_SHOWN -> subList(0, size)
        else -> subList(0, NUM_TIMERS_SHOWN)
    }
}