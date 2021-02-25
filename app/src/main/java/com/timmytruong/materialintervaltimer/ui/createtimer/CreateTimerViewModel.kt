package com.timmytruong.materialintervaltimer.ui.createtimer

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType
import com.timmytruong.materialintervaltimer.utils.events.INPUT_ERROR
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

internal const val COMPLETION_EVENT = "completion"

@ActivityRetainedScoped
class CreateTimerViewModel @Inject constructor(
    private val timerLocalDataSource: TimerRepository,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private val _timer: MutableLiveData<Timer> = MutableLiveData(Timer())
    val timer: LiveData<Timer> get() = _timer

    private val _interval: MutableLiveData<Interval> = MutableLiveData(Interval())
    val interval: LiveData<Interval> get() = _interval

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
            setEvent(INPUT_ERROR)
            return
        }

        curTimer?.apply {
            this.timer_title = title
            this.timer_total_time_ms = getTotalTimeMilliseconds()
            this.timer_created_date = DesignUtils.getCurrentDate()
            this.timer_updated_date = DesignUtils.getCurrentDate()
        }

        viewModelScope.launch(Dispatchers.Main) {
            curTimer?.let {
                val id = timerLocalDataSource.saveNewTimer(timer = curTimer)
                it.id = id.toInt()
                _timer.value = it
                setEvent(COMPLETION_EVENT)
            }
        }
    }

    fun setIntervalTitle(title: String) {
        if (title.isEmpty()) {
            setEvent(INPUT_ERROR)
            return
        }

        val curInterval = interval.value
        curInterval?.interval_name = title
        _interval.value = curInterval
        setEvent(COMPLETION_EVENT)
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
            this.interval_time_ms =
                DesignUtils.getSecondsFromTime(time = curTimeFormat ?: "") * 1000
            this.interval_time_format = DesignUtils.formatNormalizedTime(
                time = curTimeFormat ?: "",
                format = context.getString(R.string.timeFormat)
            )
        }

        curInterval?.let {
            curIntervals?.add(it)
        }

        curIntervals?.let {
            curTimer.timer_intervals = it
            curTimer.timer_intervals_count = it.size
        }

        _timer.value = curTimer
        setEvent(COMPLETION_EVENT)
        clearInterval()
    }

    fun setIntervalIcon(id: Int?) {
        val curInterval = interval.value
        curInterval?.interval_icon_id = id ?: -1
        _interval.value = curInterval
    }

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
}