package com.timmytruong.materialintervaltimer.ui.timer

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.ExtensionFunctions.forceRefresh
import com.timmytruong.materialintervaltimer.utils.enums.TimerState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@ActivityRetainedScoped
class TimerViewModel @Inject constructor(
    private val timerRepository: TimerRepository
): BaseViewModel() {
    private lateinit var timer: Timer

    private lateinit var currentInterval: Interval

    private var isAlreadySaved: Boolean = false

    private var saveAtEnd: Boolean = false

    val intervals = MutableLiveData<ArrayList<Interval>>()

    val timerState = MutableLiveData(TimerState.STOPPED)

    val progressBar = MutableLiveData(100)

    val time = MutableLiveData("")

    val intervalSound = MutableLiveData<IntervalSound>()

    val countDownTimer = MutableLiveData<CountDownTimer>()

    fun getTimer(id: Int) {
        viewModelScope.launch (Dispatchers.Main) {
            timer = timerRepository.getTimerById(id = id)
            isAlreadySaved = timer.timer_saved
            saveAtEnd = timer.timer_saved
            intervalSound.value = timer.timer_interval_sound
            intervals.value = timer.timer_intervals
            currentInterval = timer.timer_intervals[0]
            setTime(currentInterval.interval_time_secs.toInt())
            setProgressBar(currentInterval.interval_time_secs.toInt())
        }
    }

    private fun resetTimer() {
        intervalSound.value = timer.timer_interval_sound
        intervals.value = timer.timer_intervals
        currentInterval = timer.timer_intervals[0]
        setTime(currentInterval.interval_time_secs.toInt())
        setProgressBar(currentInterval.interval_time_secs.toInt())
    }

    fun setTimerState(timerState: TimerState) {
        when {
            timerState == TimerState.PAUSED -> {
                countDownTimer.value?.cancel()
                countDownTimer.value = null
                this.timerState.value = timerState
                buildIntervalTimer(time.value?.toInt() ?: 0)
            }
            timerState == TimerState.STOPPED ||
            this.timerState.value == TimerState.STOPPED -> {
                countDownTimer.value?.cancel()
                countDownTimer.value = null
                resetTimer()
                this.timerState.value = timerState
                buildIntervalTimer(currentInterval.interval_time_secs.toInt() ?: 0)
            }
            timerState == TimerState.RUNNING -> {
                this.timerState.value = timerState
                countDownTimer.forceRefresh()
            }
        }
    }

    fun setProgressBar(secondsRemaining: Int = currentInterval.interval_time_secs.toInt()) {
        val intervalTimeSeconds = currentInterval.interval_time_secs.toInt()
        progressBar.value = ((secondsRemaining.toFloat() / intervalTimeSeconds.toFloat()) * 100).roundToInt()
    }

    fun setTime(secondsRemaining: Int = currentInterval.interval_time_secs.toInt()) {
        val newTime = DesignUtils.getTimeFromSeconds(seconds = secondsRemaining)
        time.value = newTime
    }

    fun intervalTimerFinished() {
        popInterval()
        playSound()
        if (!isIntervalsEmpty()) {
            intervals.value?.let { currentInterval = it[0] }
            buildIntervalTimer(seconds = currentInterval.interval_time_secs.toInt())
        } else {
            setTimerState(timerState = TimerState.STOPPED)
            resetTimer()
        }
    }

    fun setSound(muted: Boolean) {
        if (muted) {
            intervalSound.value = null
        } else {
            intervalSound.value = timer.timer_interval_sound
        }
    }

    fun setSaved(save: Boolean) { saveAtEnd = save }

    fun saveIfChecked() {
        if (saveAtEnd && !isAlreadySaved) {
            viewModelScope.launch(Dispatchers.Main) {
                timerRepository.saveNewTimer(timer = timer)
            }
        }
    }

    private fun playSound() { intervalSound.forceRefresh() }

    private fun isIntervalsEmpty(): Boolean = intervals.value?.isEmpty() ?: true

    private fun popInterval() {
        val tempList = arrayListOf<Interval>()
        tempList.addAll(intervals.value ?: arrayListOf())
        if (tempList.size > 0) tempList.removeAt(0)
        intervals.value = tempList
    }

    private fun buildIntervalTimer(seconds: Int) {
        val milliseconds = (seconds * 1000).toLong()
        countDownTimer.value = object : CountDownTimer(milliseconds, 500) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished.toInt() / 1000
                setTime(secondsRemaining = secondsRemaining)
                setProgressBar(secondsRemaining = secondsRemaining)
            }

            override fun onFinish() {
                if (timerState.value == TimerState.RUNNING) {
                    intervalTimerFinished()
                }
            }
        }
    }
}