package com.timmytruong.materialintervaltimer.ui.timer

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.IntervalItem
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.toListItems
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.IntervalTimer
import com.timmytruong.materialintervaltimer.utils.TimerState
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_SECS_L
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val intervalTimer: IntervalTimer,
    private val timerLocalDataSource: TimerRepository,
    private val directions: TimerDirections
) : BaseViewModel() {

    private val _intervals = MutableSharedFlow<List<IntervalItem>>()
    val intervals: SharedFlow<List<IntervalItem>> = _intervals

    val timerState: StateFlow<TimerState> = intervalTimer.timerState

    val timeRemaining: StateFlow<Long> = intervalTimer.currentTimeRemaining

    var isMuted: Boolean = false

    private lateinit var timer: Timer

    private val currentIntervalTimeRemaining: Long
        get() = intervalTimer.currentTimeRemaining.value

    private val progress: Float
        get() = (currentIntervalTimeRemaining.toFloat() / currentIntervalTotalTime.toFloat()) * MILLI_IN_SECS_L

    private val currentIntervalTotalTime: Long
        get() = intervalTimer.currentIntervalTotalTime

    private val shouldRepeatIntervals: Boolean
        get() = timer.shouldRepeat &&
                (intervalItems.size <= timer.intervalCount / 2 || intervalItems.size == 1)

    private val intervalItems: MutableList<IntervalItem> = mutableListOf()

    fun fetchTimer(id: Int) = startSuspending(ioDispatcher) { scope ->
        timer = timerLocalDataSource.getTimerById(id = id)
        intervalTimer.currentTimeRemaining.onEach { onTick(it) }.launchIn(scope)
        intervalTimer.load(timer.intervals.map { it.timeMs })
        Event.Timer.IsSaved(timer.isFavorited).fire()
        Event.Timer.HasSound(timer.intervalSound.id != -1).fire()
        updateIntervalBindings()
    }

    private suspend fun onTick(ms: Long) {
        if (shouldRepeatIntervals) addRepeatIntervals()
        if (ms == 0L) onIntervalFinished()
    }

    fun handlePlay() = startSuspending(ioDispatcher) {
        Event.Timer.CancelAnimation.fire()
        intervalTimer.start()
        Event.Timer.Started(intervalTimer.currentTimeRemaining.value, progress).fire()
    }

    fun handlePause() = startSuspending(ioDispatcher) {
        Event.Timer.CancelAnimation.fire()
        intervalTimer.pause()
        Event.Timer.Progress(progress).fire()
    }

    fun handleStop() = startSuspending(ioDispatcher) {
        Event.Timer.CancelAnimation.fire()
        intervalTimer.stop()
        updateIntervalBindings()
        Event.Timer.Progress(progress).fire()
    }

    fun setShouldSave() = startSuspending(ioDispatcher) {
        timerLocalDataSource.setFavorite(id = timer.id, favorite = !timer.isFavorited)
    }

    fun exit() {
        handleStop()
        Event.Navigate(directions.toHome()).fire()
    }

    private suspend fun addRepeatIntervals() {
        intervalItems.addAll(timer.intervals.toListItems(hasHeaders = true))
        _intervals.emit(intervalItems)
    }

    private fun updateIntervalBindings() = startSuspending(ioDispatcher) {
        intervalItems.clear()
        intervalItems.addAll(timer.intervals.toListItems(hasHeaders = true))
        _intervals.emit(intervalItems)
    }

    private fun onIntervalFinished() = startSuspending(ioDispatcher) {
        if (!intervalTimer.isFinished) {
            popFinishedInterval()
            playSound()
            intervalTimer.next()
            Event.Timer.Progress(progress).fire()
            Event.Timer.Started(intervalTimer.currentTimeRemaining.value, progress).fire()
        } else {
            handleStop()
        }
    }

    private suspend fun popFinishedInterval() {
        if (intervalItems.isNotEmpty()) {
            intervalItems.removeFirst()
            _intervals.emit(intervalItems)
        }
    }

    private fun playSound() = startSuspending(mainDispatcher) {
        val soundId = timer.intervalSound.id
        if (!isMuted && soundId != -1) Event.PlaySound(soundId).fire()
    }
}