package com.timmytruong.materialintervaltimer.ui.timer

import android.media.MediaPlayer
import androidx.databinding.ObservableInt
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.IntervalItemScreenBinding
import com.timmytruong.materialintervaltimer.utils.*
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_SECS_L
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal const val IS_SAVED = "is saved"
internal const val CANCEL_ANIMATION = "cancel anim"
internal const val START_ANIMATION = "start animation"

@HiltViewModel
class TimerViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val intervalTimer: IntervalTimer,
    private val timerLocalDataSource: TimerRepository,
    private val screen: TimerScreen,
    private val resources: ResourceProvider
) : BaseViewModel() {

    var isMuted: Boolean = false

    private lateinit var timer: Timer
    private val intervalBindings = MutableStateFlow<List<IntervalItemScreenBinding>>(listOf())
    private var currentIntervalTimeRemaining: Long = 0L
    private var currentIntervalTotalTime: Long = 0L
    private var intervals: ArrayList<Interval> = arrayListOf()
        set(value) {
            field = value
            updateIntervalBindings()
        }

    fun fetchTimer(id: Int) = startSuspending(ioDispatcher) {
        if (!(this@TimerViewModel::timer.isInitialized)) {
            timer = timerLocalDataSource.getTimerById(id = id)
        }

        screen.intervals = intervalBindings
        intervalTimer.currentTimeRemaining.collectLatest(::updateTimeRemaining)
        fireEvents(IS_SAVED to timer.isFavourited)
        handleStop()
    }

    fun handlePlay() = startSuspending(ioDispatcher) {
        cancelAnimation()
        screen.timerState.set(TimerState.RUNNING)
        intervalTimer.start()
        fireEvents(START_ANIMATION to currentIntervalTimeRemaining)
    }

    fun handlePause() = startSuspending(ioDispatcher) {
        cancelAnimation()
        screen.timerState.set(TimerState.PAUSED)
        intervalTimer.onTimerPaused()
        resetProgress()
    }

    fun handleStop() = startSuspending(ioDispatcher) {
        cancelAnimation()
        screen.timerState.set(TimerState.STOPPED)
        intervalTimer.clearTimer()
        setNewIntervalList()
    }

    fun setShouldSave(favourite: Boolean) = startSuspending(ioDispatcher) {
        timerLocalDataSource.setFavourite(id = timer.id, favourite = favourite)
    }

    fun exit() {
        handleStop()
        navigateWith(screen.navToHome())
    }

    private fun cancelAnimation() = fireEvents(CANCEL_ANIMATION to Unit)

    private fun resetProgress() {
        screen.progress.set(((currentIntervalTimeRemaining.toFloat() / currentIntervalTotalTime.toFloat()) * MILLI_IN_SECS_L).toInt())
    }

    private suspend fun setNewInterval() {
        val intervalTime = intervals.first().timeMs
        intervalTimer.buildIntervalTimer(intervalTime)
        currentIntervalTimeRemaining = intervalTime
        currentIntervalTotalTime = currentIntervalTimeRemaining
        resetProgress()
    }

    private fun addRepeatIntervals() {
        intervals.addAll(timer.intervals)
        updateIntervalBindings()
    }

    private fun updateIntervalBindings() = startSuspending(ioDispatcher) {
        intervalBindings.value = intervals.map { interval ->
            IntervalItemScreenBinding(
                iconId = ObservableInt(interval.iconId),
                title = ObservableString(interval.name),
                description = ObservableString(interval.timeMs.toDisplayTime(resources))
            )
        }
    }

    private fun updateTimeRemaining(timeRemaining: Long) {
        setTimeRemaining(timeRemaining)

        if (timer.shouldRepeat && (intervals.size <= timer.intervalCount / 2 || intervals.size == 1)) {
            addRepeatIntervals()
        }

        if (timeRemaining == 0L) onIntervalFinished()
    }

    private fun setTimeRemaining(timeRemaining: Long) {
        currentIntervalTimeRemaining = timeRemaining
        screen.timeRemaining.set(currentIntervalTimeRemaining.toDisplayTime(resources))
    }

    private fun onIntervalFinished() = startSuspending(ioDispatcher) {
        popFinishedInterval()
        playSound()

        if (intervals.isEmpty()) {
            handleStop()
        } else {
            setNewInterval()
            handlePlay()
        }
    }

    private fun popFinishedInterval() {
        if (intervals.isNotEmpty()) {
            intervals.removeFirst()
            updateIntervalBindings()
        }
    }

    private fun playSound() {
        val soundId = timer.intervalSound.id
        if (!isMuted && soundId != -1) {
            MediaPlayer.create(resources.ctx, soundId).start()
        }
    }

    private suspend fun setNewIntervalList() {
        intervals.clear()
        intervals.addAll(timer.intervals)
        updateIntervalBindings()
        setNewInterval()
    }
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class TimerViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideTimerScreen() = TimerScreen()
}

enum class TimerState {
    STOPPED,
    PAUSED,
    RUNNING
}