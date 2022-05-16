package com.timmytruong.materialintervaltimer.ui.create.interval.time

import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.IntervalStore
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.data.model.Interval
import com.timmytruong.materialintervaltimer.data.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.base.adapter.EmptyClicks
import com.timmytruong.materialintervaltimer.ui.create.interval.time.grid.NumberItem
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.extensions.getTimeMs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class CreateIntervalTimeViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @TimerStore private val timerStore: Store<Timer>,
    @IntervalStore private val intervalStore: Store<Interval>,
    private val directions: CreateIntervalTimeDirections
) : BaseViewModel() {

    private val _numbers = MutableSharedFlow<List<NumberItem>>(replay = 1)
    val numbers: Flow<List<NumberItem>> = _numbers

    private val _time = MutableStateFlow("")
    val time: Flow<String> = _time

    private var input: String = ""

    fun removeFromTime() = startSuspending(ioDispatcher) {
        if (input.isNotEmpty()) {
            input = input.dropLast(1)
            _time.value = input
        }
    }

    fun addInterval() = startSuspending(ioDispatcher) {
        intervalStore.update { it.timeMs = input.getTimeMs() }

        timerStore.update {
            it.intervals.add(intervalStore.get().copy())
            it.intervalCount = it.intervals.size
        }

        Event.Navigate(directions.navToCreateTimer()).fire()
    }

    fun backPressed() = Event.Navigate(directions.navToCreateInterval()).fire()

    fun createNumbers() = startSuspending(ioDispatcher) {
        val numberItems = mutableListOf<NumberItem>()

        for (num in 1..9) { numberItems.add(NumberItem(number = num) { addToTime(num) }) }
        numberItems.add(NumberItem(clicks = EmptyClicks))
        numberItems.add(NumberItem(number = 0) { addToTime(0) })
        numberItems.add(NumberItem(clicks = EmptyClicks))

        _numbers.emit(numberItems)
    }

    private fun addToTime(newNumber: Int) = startSuspending(ioDispatcher) {
        if (input.isEmpty() && newNumber == 0) return@startSuspending
        if (input.length <= 6) {
            input = "$input$newNumber"
            _time.value = input
        }
    }
}