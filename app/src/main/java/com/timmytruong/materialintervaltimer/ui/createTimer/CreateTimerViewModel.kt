package com.timmytruong.materialintervaltimer.ui.createTimer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.constants.AppConstants
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class CreateTimerViewModel @Inject constructor(
    private val timerRepository: TimerRepository
) : BaseViewModel() {
    val timer: MutableLiveData<Timer> = MutableLiveData(Timer())

    val timerTitle: MutableLiveData<String> = MutableLiveData("")

    val intervals: MutableLiveData<ArrayList<Interval>> = MutableLiveData(arrayListOf())

    val selectedSound: MutableLiveData<IntervalSound> = MutableLiveData(IntervalSound())

    val repeatChecked: MutableLiveData<Boolean> = MutableLiveData(false)

    val savedChecked: MutableLiveData<Boolean> = MutableLiveData(false)

    val intervalTime: MutableLiveData<String> = MutableLiveData("")

    val intervalTitle: MutableLiveData<String> = MutableLiveData("")

    val intervalIconChecked: MutableLiveData<Boolean> = MutableLiveData(false)

    val intervalIconId: MutableLiveData<Int> = MutableLiveData(0)

    fun clearTimer() {
        timer.value = null
        timerTitle.value = ""
        intervals.value = arrayListOf()
        selectedSound.value = IntervalSound()
        repeatChecked.value = false
        savedChecked.value = false
        clearInterval()
    }

    private fun clearInterval() {
        intervalTime.value = ""
        intervalTitle.value = ""
        intervalIconChecked.value = false
        intervalIconId.value = 0
    }

    fun getSelectedSound(): IntervalSound? = selectedSound.value

    fun setSelectedSound(intervalSound: IntervalSound?) {
        if (intervalSound == null) return

        val sounds = AppConstants.SOUNDS

        sounds.forEach {
            if (intervalSound.sound_id == it.sound_id) {
                selectedSound.value = intervalSound
            }

            it.sound_is_selected = intervalSound.sound_id == it.sound_id
        }
    }

    fun setTimerTitle(title: String) {
        timerTitle.value = title
    }

    fun setRepeat(checked: Boolean) {
        repeatChecked.value = checked
    }

    fun setSaved(checked: Boolean) {
        savedChecked.value = checked
    }

    fun validateTimer(title: String) {
        if (intervals.value?.size == 0) {
            setError(errorType = ErrorType.EMPTY_INPUT)
            return
        }

        val timer = timer.value ?: Timer()
        timer.apply {
            timer_interval_sound = selectedSound.value ?: AppConstants.SOUNDS[0]
            timer_title = title
            timer_created_date = DesignUtils.getCurrentDate()
            timer_saved = savedChecked.value ?: false
            timer_repeat = repeatChecked.value ?: false
            timer_intervals = intervals.value ?: arrayListOf()
            timer_intervals_count = intervals.value?.size.toString()
            timer_total_time_secs = getTotalTimeSeconds().toString()
        }

        viewModelScope.launch(Dispatchers.Main) {
            timer.let {
                val id = timerRepository.saveNewTimer(timer = timer)
                it.id = id.toInt()
                this@CreateTimerViewModel.timer.value = it
            }
        }
    }

    private fun getTotalTimeSeconds(): Int {
        var totalTime = 0
        for (interval in intervals.value ?: arrayListOf()) {
            totalTime += interval.interval_time_secs.toInt()
        }
        return totalTime
    }

    fun setIntervalTitle(title: String) {
        intervalTitle.value = title
    }

    fun addToTime(addition: String) {
        if (intervalTime.value?.length ?: 0 > 5) return
        intervalTime.value = "${intervalTime.value}${addition}"
    }

    fun removeFromTime() {
        if (intervalTime.value?.length ?: 0 == 0) return
        val oldTime = intervalTime.value

        oldTime?.let {
            intervalTime.value = it.dropLast(1)
        }
    }

    fun addInterval() {
        val tempList = intervals.value ?: arrayListOf()

        tempList.add(
            Interval(
                interval_time_format = DesignUtils.formatNormalizedTime(intervalTime.value ?: "", AppConstants.TIME_FORMAT),
                interval_time_secs = DesignUtils.getSecondsFromTime(time = intervalTime.value ?: "").toString(),
                interval_name = intervalTitle.value ?: "",
                interval_icon_id = if (intervalIconChecked.value == true) intervalIconId.value.toString() else "0"
            )
        )

        intervals.value = tempList
        clearInterval()
    }

    fun setIntervalIconChecked(checked: Boolean) {
        intervalIconChecked.value = checked
    }

    fun setIntervalIcon(id: Int) {
        intervalIconId.value = id
    }
}