package com.timmytruong.materialintervaltimer.ui.create.interval.time

import com.timmytruong.data.di.BackgroundDispatcher
import com.timmytruong.data.di.IntervalStore
import com.timmytruong.data.di.MainDispatcher
import com.timmytruong.data.di.TimerStore
import com.timmytruong.data.local.Store
import com.timmytruong.materialintervaltimer.data.model.Interval
import com.timmytruong.materialintervaltimer.data.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.extensions.getTimeMs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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

    fun addToTime(number: Int) = startSuspending(ioDispatcher) {
        if (input.isEmpty() && number == 0) return@startSuspending
        if (input.length <= 6) {
            input = "$input$number"
            _time.value = input
        }
    }
}