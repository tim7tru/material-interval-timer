package com.timmytruong.materialintervaltimer.ui.list

import com.timmytruong.data.TimerRepository
import com.timmytruong.data.di.BackgroundDispatcher
import com.timmytruong.data.di.MainDispatcher
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.reusable.item.TimerItem
import com.timmytruong.materialintervaltimer.ui.reusable.item.toTimerItems
import com.timmytruong.materialintervaltimer.ui.reusable.type.TimerType
import com.timmytruong.materialintervaltimer.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import javax.inject.Inject

@HiltViewModel
class TimerListViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val directions: TimerListDirctions
) : BaseViewModel() {

    private val _timers = MutableSharedFlow<List<TimerItem>>(replay = 1)
    val timers: Flow<List<TimerItem>> = _timers

    fun fetchTimers(type: TimerType) = startSuspending(ioDispatcher) {
        _timers.emitAll(
            when (type) {
                TimerType.FAVORITES -> timerRepository.getFavoritedTimers().toTimerItems(onTimerClicked = ::onTimerClicked)
                TimerType.RECENTS -> timerRepository.getRecentTimers().toTimerItems(onTimerClicked = ::onTimerClicked)
            }
        )
    }

    private fun onTimerClicked(id: Int, favorited: Boolean) = Event.Navigate(directions.navToBottomSheet(id, favorited)).fire()
}