package com.timmytruong.materialintervaltimer.utils

import android.os.CountDownTimer
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TICK_INTERVAL_MS = 100L

@ActivityRetainedScoped
class IntervalTimer @Inject constructor(
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) {

    private val _currentTimeRemaining = MutableStateFlow(0L)
    val currentTimeRemaining: StateFlow<Long> = _currentTimeRemaining

    private var countDownTimer: CountDownTimer? = null

    suspend fun start() = withContext(mainDispatcher) { countDownTimer?.start() }

    suspend fun clearTimer() = withContext(mainDispatcher) {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    suspend fun onTimerPaused() = withContext(mainDispatcher) {
        clearTimer()
        buildIntervalTimer(_currentTimeRemaining.value)
    }

    suspend fun buildIntervalTimer(time: Long) = withContext(mainDispatcher) {
        _currentTimeRemaining.value = time
        countDownTimer = object : CountDownTimer(time, TICK_INTERVAL_MS) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTimeRemaining.value = millisUntilFinished
            }

            override fun onFinish() {
                _currentTimeRemaining.value = 0L
            }
        }
    }
}