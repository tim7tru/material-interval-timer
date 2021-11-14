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

    private val _timerState = MutableStateFlow(TimerState.STOPPED)
    val timerState: Flow<TimerState> = _timerState

    private val _timeRemaining = MutableStateFlow(0L)
    val timeRemaining: Flow<Long> = _timeRemaining

    private val _progress = MutableStateFlow(1000f)
    val progress: Flow<Float> = _progress

    private val _intervals = MutableSharedFlow<List<IntervalItem>>()
    val intervals: Flow<List<IntervalItem>> = _intervals

    var isMuted: Boolean = false

    private lateinit var timer: Timer
    private var currentIntervalTimeRemaining: Long = 0L
    private var currentIntervalTotalTime: Long = 0L
    private var intervalItems: MutableList<IntervalItem> = mutableListOf()

    fun fetchTimer(id: Int) = startSuspending(ioDispatcher) { scope ->
        timer = timerLocalDataSource.getTimerById(id = id)
        intervalTimer.currentTimeRemaining.onEach { updateTimeRemaining(it) }.launchIn(scope)
        fireEvent(
            Event.Timer.IsSaved(timer.isFavorited),
            Event.Timer.HasSound(timer.intervalSound.id != -1)
        )
        handleStop()
    }

    fun handlePlay() = startSuspending(ioDispatcher) {
        cancelAnimation()
        _timerState.value = TimerState.RUNNING
        intervalTimer.start()
        fireEvent(Event.Timer.Started(currentIntervalTimeRemaining, _progress.value))
    }

    fun handlePause() = startSuspending(ioDispatcher) {
        cancelAnimation()
        _timerState.value = TimerState.PAUSED
        intervalTimer.onTimerPaused()
        resetProgress()
    }

    fun handleStop() = startSuspending(ioDispatcher) {
        cancelAnimation()
        _timerState.value = TimerState.STOPPED
        intervalTimer.clearTimer()
        setNewIntervalList()
    }

    fun setShouldSave() = startSuspending(ioDispatcher) {
        timerLocalDataSource.setFavorite(id = timer.id, favorite = !timer.isFavorited)
    }

    fun exit() {
        handleStop()
        navigateWith(directions.toHome())
    }

    private fun cancelAnimation() = fireEvent(Event.Timer.Stopped)

    private fun resetProgress() {
        val progress = ((currentIntervalTimeRemaining.toFloat() / currentIntervalTotalTime.toFloat()) * MILLI_IN_SECS_L)
        _progress.value = progress
    }

    private fun setNewInterval() = startSuspending(mainDispatcher) {
        val intervalTime = timer.intervals.first().timeMs
        intervalTimer.buildIntervalTimer(intervalTime)
        currentIntervalTimeRemaining = intervalTime
        currentIntervalTotalTime = currentIntervalTimeRemaining
        resetProgress()
    }

    private fun addRepeatIntervals() {
        intervalItems.addAll(timer.intervals.toListItems(hasHeaders = true))
        updateIntervalBindings()
    }

    private fun updateIntervalBindings() = startSuspending(ioDispatcher) {
        intervalItems.clear()
        intervalItems.addAll(timer.intervals.toListItems(hasHeaders = true))
        fireEvent(Event.Timer.Bindings)
    }

    private fun updateTimeRemaining(timeRemaining: Long) {
        setTimeRemaining(timeRemaining)

        if (timer.shouldRepeat && (intervalItems.size <= timer.intervalCount / 2 || intervalItems.size == 1)) {
            addRepeatIntervals()
        }

        if (timeRemaining == 0L) onIntervalFinished()
    }

    private fun setTimeRemaining(timeRemaining: Long) { _timeRemaining.value = timeRemaining }

    private fun onIntervalFinished() = startSuspending(ioDispatcher) {
        popFinishedInterval()
        playSound()

        if (intervalItems.isEmpty()) {
            handleStop()
        } else {
            setNewInterval()
            handlePlay()
        }
    }

    private fun popFinishedInterval() {
        if (intervalItems.isNotEmpty()) {
            intervalItems.removeFirst()
            updateIntervalBindings()
        }
    }

    private fun playSound() = startSuspending(mainDispatcher) {
        val soundId = timer.intervalSound.id
        if (!isMuted && soundId != -1) fireEvent(Event.PlaySound(soundId))
    }

    private suspend fun setNewIntervalList() {
        intervalItems.clear()
        intervalItems.addAll(timer.intervals.toListItems(hasHeaders = true))
        updateIntervalBindings()
        setNewInterval()
    }
}

enum class TimerState {
    STOPPED,
    PAUSED,
    RUNNING
}