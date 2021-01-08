package com.timmytruong.materialintervaltimer.ui.createTimer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.constants.AppConstants
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class CreateTimerViewModel @Inject constructor(
    private val timerRepository: TimerRepository
) : BaseViewModel() {

    private val _completionEvent = MutableLiveData<Event<Boolean>>()
    val completionEvent: LiveData<Event<Boolean>> get() = _completionEvent

    private val _timer: MutableLiveData<Timer> = MutableLiveData(Timer())
    val timer: LiveData<Timer> get() = _timer

    private val _interval: MutableLiveData<Interval> = MutableLiveData(Interval())
    val interval: LiveData<Interval> get() = _interval

    private fun clearInterval() {
        _interval.value = Interval()
    }

    private fun getTotalTimeMilliseconds(): Int {
        val curTimer = timer.value
        val curIntervals = curTimer?.timer_intervals
        var totalTimeMilliseconds = 0
        curIntervals?.let {
            for (interval in it) {
                totalTimeMilliseconds += interval.interval_time_ms
            }
        }
        return totalTimeMilliseconds
    }

    private fun setCompletion(isComplete: Boolean) {
        _completionEvent.value = Event(isComplete)
    }

    fun clearTimer() {
        _timer.value = Timer()
        clearInterval()
    }

    fun setSelectedSound(intervalSound: IntervalSound) {
        val curTimer = timer.value
        curTimer?.timer_interval_sound = intervalSound
        _timer.value = curTimer
    }

    fun setTimerTitle(title: String) {
        val curTimer = timer.value
        curTimer?.timer_title = title
        _timer.value = curTimer
    }

    fun setRepeat(checked: Boolean) {
        val curTimer = timer.value
        curTimer?.timer_repeat = checked
        _timer.value = curTimer
    }

    fun setSaved(checked: Boolean) {
        val curTimer = timer.value
        curTimer?.timer_saved = checked
        _timer.value = curTimer
    }

    fun validateTimer(title: String) {
        val curTimer = timer.value

        if (curTimer?.timer_intervals.isNullOrEmpty()) {
            setError(errorType = ErrorType.EMPTY_INPUT)
            return
        }

        curTimer?.apply {
            this.timer_title = title
            this.timer_total_time_ms = getTotalTimeMilliseconds()
            this.timer_created_date = DesignUtils.getCurrentDate()
        }

        viewModelScope.launch(Dispatchers.Main) {
            curTimer?.let {
                val id = timerRepository.saveNewTimer(timer = curTimer)
                it.id = id.toInt()
                _timer.value = it
                _completionEvent.value = Event(true)
            }
        }
    }

    fun setIntervalTitle(title: String) {
        if (title.isEmpty()) {
            setError(errorType = ErrorType.EMPTY_INPUT)
            return
        }

        val curInterval = interval.value
        curInterval?.interval_name = title
        _interval.value = curInterval
        setCompletion(isComplete = true)
    }

    fun addToTime(newNumber: String) {
        val curInterval = interval.value
        val curTime = curInterval?.interval_time_format
        if (curTime?.length ?: 0 < 6) {
            curInterval?.interval_time_format = "${curTime}${newNumber}"
            _interval.value = curInterval
        }
    }

    fun removeFromTime() {
        val curInterval = interval.value
        val curTime = curInterval?.interval_time_format
        if (curTime?.length ?: 0 != 0) {
            curInterval?.interval_time_format = curTime?.dropLast(1).toString()
            _interval.value = curInterval
        }
    }

    fun addInterval() {
        val curTimer = timer.value
        val curIntervals = curTimer?.timer_intervals
        val curInterval = interval.value
        val curTimeFormat = curInterval?.interval_time_format

        curInterval?.apply {
            this.interval_time_ms = DesignUtils.getSecondsFromTime(time = curTimeFormat ?: "") * 1000
            this.interval_time_format = DesignUtils.formatNormalizedTime(time = curTimeFormat ?: "", format = AppConstants.TIME_FORMAT)
        }

        curInterval?.let {
            curIntervals?.add(it)
        }

        curIntervals?.let {
            curTimer.timer_intervals = it
            curTimer.timer_intervals_count = it.size
        }

        _timer.value = curTimer
        setCompletion(isComplete = true)
        clearInterval()
    }

    fun setIntervalIcon(id: Int?) {
        val curInterval = interval.value
        curInterval?.interval_icon_id = id ?: -1
        _interval.value = curInterval
    }
}