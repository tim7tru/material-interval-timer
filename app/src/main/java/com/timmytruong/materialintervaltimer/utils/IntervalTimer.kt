package com.timmytruong.materialintervaltimer.utils

import android.os.CountDownTimer
import com.timmytruong.data.di.MainDispatcher
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TICK_INTERVAL_MS = 10L

@ActivityRetainedScoped
class IntervalTimer @Inject constructor(
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val tickInterval: Long = TICK_INTERVAL_MS
) {

    private val _timerState = MutableStateFlow(TimerState.STOPPED)
    val timerState: StateFlow<TimerState> = _timerState

    private val _currentTimeRemaining = MutableStateFlow(0L)
    val currentTimeRemaining: StateFlow<Long> = _currentTimeRemaining

    private var _timer: CountDownTimer? = null

    private var currentIntervalNumber: Int = 0

    private val _intervals: ArrayList<Long> = arrayListOf()

    val currentIntervalTotalTime: Long
        get() = _intervals[currentIntervalNumber]

    val isFinished: Boolean
        get() = currentIntervalNumber >= _intervals.size

    suspend fun start() = withContext(mainDispatcher) {
        _timerState.value = TimerState.RUNNING
        _timer?.start()
    }

    suspend fun stop() = withContext(mainDispatcher) {
        _timerState.value = TimerState.STOPPED
        currentIntervalNumber = 0
        cancel()
        build()
    }

    suspend fun pause() = withContext(mainDispatcher) {
        _timerState.value = TimerState.PAUSED
        cancel()
        build(currentTimeRemaining.value)
    }

    suspend fun load(intervals: List<Long>) = withContext(mainDispatcher) {
        _intervals.clear()
        _intervals.addAll(intervals)
        build()
    }

    suspend fun next() = withContext(mainDispatcher) {
        if (isFinished) stop()
        else {
            build()
            start()
        }
    }

    private fun cancel() {
        _timer?.cancel()
        _timer = null
    }

    private fun build(time: Long? = null) {
        val intervalTime = time ?: _intervals[currentIntervalNumber]
        _currentTimeRemaining.value = intervalTime

        _timer = object : CountDownTimer(intervalTime, tickInterval) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTimeRemaining.value = millisUntilFinished
            }

            override fun onFinish() {
                currentIntervalNumber++
                _currentTimeRemaining.value = 0L
            }
        }
    }
}

enum class TimerState {
    STOPPED,
    PAUSED,
    RUNNING
}