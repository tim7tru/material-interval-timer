package com.timmytruong.materialintervaltimer.ui.createtimer

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.NAVIGATE
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.createtimer.fragments.CreateIntervalFragmentDirections
import com.timmytruong.materialintervaltimer.ui.createtimer.fragments.CreateIntervalTimeFragmentDirections
import com.timmytruong.materialintervaltimer.ui.createtimer.fragments.CreateTimerFragmentDirections
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.DesignUtils.getDrawableIdFromTag
import com.timmytruong.materialintervaltimer.utils.events.INPUT_ERROR
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Qualifier

internal const val SOUND_DESELECTED = "sound deselected"
internal const val SOUND_SELECTED = "sound selected"
internal const val CLOSE_SOUNDS = "close sounds"

@ActivityRetainedScoped
class CreateTimerViewModel @Inject constructor(
    private val timerLocalDataSource: TimerRepository,
    private val sounds: List<IntervalSound>,
    @ApplicationContext private val context: Context,
    private val navToTimer: CreateTimerFragmentDirections.ActionCreateTimerFragmentToTimerFragment,
    @CreateToAddInterval private val navToAdd: NavDirections,
    @CreateToSoundsBottomSheet private val navToSound: NavDirections,
    @IntervalToIntervalTime private val navToIntervalTime: NavDirections,
    private val navToCreateTimer: CreateIntervalTimeFragmentDirections.ActionCreateIntervalTimeFragmentToCreateTimerFragment
) : BaseViewModel() {

    private var soundSelected: Int = 0

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

    fun setIntervalTitle(title: String) {
        if (title.isEmpty()) {
            setEvent(INPUT_ERROR)
            return
        }

        val curInterval = interval.value
        curInterval?.interval_name = title
        _interval.value = curInterval
        setEvent(NAVIGATE, navToIntervalTime)
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

    fun dismissSoundsBottomSheet() = setEvent(CLOSE_SOUNDS)

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
        setEvent(NAVIGATE, navToCreateTimer)
        clearInterval()
    }

    fun setIntervalIcon(tag: String) {
        val id = getDrawableIdFromTag(context, tag)
        val curInterval = interval.value
        curInterval?.interval_icon_id = id
        _interval.value = curInterval
    }

    fun onGoToAddIntervalClicked() = setEvent(NAVIGATE, navToAdd)

    fun onGoToTimerClicked(title: String) {
        if (validateTimer(title)) {
            navToTimer.apply { timer.value?.let { timerId = it.id } }
            setEvent(NAVIGATE, navToTimer)
        } else {
            setEvent(INPUT_ERROR)
        }
    }

    fun onGoToSoundsBottomSheet() = setEvent(NAVIGATE, navToSound)

    private fun playSound() {
        val id = sounds[soundSelected].sound_id
        if (id != -1) {
            id.let { MediaPlayer.create(context, it).start() }
        }
    }

    private fun clearInterval() {
        _interval.value = Interval()
    }

    private fun getTotalTimeMilliseconds(): Int {
        val curTimer = timer.value
        val curIntervals = curTimer?.timer_intervals
        var totalTimeMilliseconds = 0
        curIntervals?.let { list ->
            list.forEach {
                totalTimeMilliseconds += it.interval_time_ms
            }
        }
        return totalTimeMilliseconds
    }

    private fun validateTimer(title: String): Boolean {
        val curTimer = timer.value

        if (curTimer?.timer_intervals.isNullOrEmpty()) {
            return false
        }

        curTimer?.apply {
            this.timer_title = title
            this.timer_total_time_ms = getTotalTimeMilliseconds()
            this.timer_created_date = DesignUtils.getCurrentDate()
            this.timer_updated_date = DesignUtils.getCurrentDate()
        }

        saveTimer(timer = curTimer)

        return true
    }

    private fun saveTimer(timer: Timer?) = viewModelScope.launch {
        timer?.let {
            val id = timerLocalDataSource.saveNewTimer(timer = it)
            it.id = id.toInt()
            _timer.value = it
            setEvent(NAVIGATE, navToIntervalTime)
        }
    }
}

@Qualifier private annotation class CreateToAddInterval
@Qualifier private annotation class CreateToSoundsBottomSheet
@Qualifier private annotation class IntervalToIntervalTime

@InstallIn(ActivityRetainedComponent::class)
@Module
class CreateTimerViewModelModule {

    @Provides
    fun provideNavToTimer() =
        CreateTimerFragmentDirections.actionCreateTimerFragmentToTimerFragment()

    @Provides
    fun provideNavBackToCreateTimer() =
        CreateIntervalTimeFragmentDirections.actionCreateIntervalTimeFragmentToCreateTimerFragment()
            .apply { clearViewModel = false }

    @Provides
    @CreateToAddInterval
    fun provideNavToAddInterval() =
        CreateTimerFragmentDirections.actionCreateTimerFragmentToCreateIntervalFragment()

    @Provides
    @CreateToSoundsBottomSheet
    fun provideNavToSoundBottomSheet() =
        CreateTimerFragmentDirections.actionCreateTimerFragmentToIntervalSoundsBottomSheet()

    @Provides
    @IntervalToIntervalTime
    fun provideNavToIntervalTime() =
        CreateIntervalFragmentDirections.actionCreateIntervalFragmentToCreateIntervalTimeFragment()
}