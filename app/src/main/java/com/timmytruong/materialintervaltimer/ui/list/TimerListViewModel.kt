package com.timmytruong.materialintervaltimer.ui.list

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.TimerItem
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.toTimerItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TimerListViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val directions: TimerListDirctions
) : BaseViewModel() {

    lateinit var timers: Flow<List<TimerItem>>

    fun fetchTimers(type: TimerType) = startSuspending(ioDispatcher) {
        timers = when (type) {
            TimerType.FAVORITES -> timerRepository.getFavoritedTimers().toTimerItems()
            TimerType.RECENTS -> timerRepository.getRecentTimers().toTimerItems()
        }
    }

    private fun Flow<List<Timer>>.toTimerItems() = map { it.toTimerItems(::onTimerClicked) }

    private fun onTimerClicked(id: Int, favorited: Boolean) = navigateWith(directions.navToBottomSheet(id, favorited))
}