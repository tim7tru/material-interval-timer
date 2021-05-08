package com.timmytruong.materialintervaltimer.ui.timer

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalItemScreenBinding
import com.timmytruong.materialintervaltimer.utils.*
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
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
    private val resources: ResourceProvider,
    private val intervalTimer: IntervalTimer,
    private val timerLocalDataSource: TimerRepository,
    private val screen: TimerScreen
) : BaseViewModel() {

    var isMuted: Boolean = false

    private lateinit var timer: Timer

    private var currentIntervalTimeRemaining: Float = 0f
    private var currentIntervalTotalTime: Float = 0f
    private var intervals: ArrayList<Interval> = arrayListOf()
        set(value) {
            field = value
            updateIntervalBindings()
        }

    private val intervalBindings = MutableStateFlow<List<IntervalItemScreenBinding>>(listOf())

    fun fetchTimer(id: Int) = startSuspending(ioDispatcher) {
        if (!(this@TimerViewModel::timer.isInitialized)) {
            timer = timerLocalDataSource.getTimerById(id = id)
        }

        screen.intervals = intervalBindings
        intervalTimer.currentTimeRemaining.map { it.toInt() }.collectLatest(::updateTimeRemaining)
        fireEvents(IS_SAVED to timer.isFavourited)
        handleStop()
    }

    fun handlePlay() = startSuspending(ioDispatcher) {
        cancelAnimation()
        screen.timerState.set(TimerState.RUNNING)
        intervalTimer.start()
        fireEvents(START_ANIMATION to currentIntervalTimeRemaining.toLong())
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
        screen.progress.set(((currentIntervalTimeRemaining / currentIntervalTotalTime) * MILLI_PER_SECOND).toInt())
    }

    private suspend fun setNewInterval() {
        val intervalTime = intervals.first().time.getTotalTime()
        intervalTimer.buildIntervalTimer(intervalTime)
        currentIntervalTimeRemaining = intervalTime.toFloat()
        currentIntervalTotalTime = currentIntervalTimeRemaining
        resetProgress()
    }

    private fun addRepeatIntervals() {
        intervals.addAll(timer.intervals)
        updateIntervalBindings()
    }

    private fun updateIntervalBindings() = startSuspending(ioDispatcher) {
        intervalBindings.value = intervals.mapIndexed { position, interval ->
            IntervalItemScreenBinding(
                hasHeader = ObservableBoolean(position == 0 || position == 1),
                header = ObservableField(
                    when (position) {
                        0 -> resources.string(R.string.currentIntervalTitle)
                        1 -> resources.string(R.string.upNextIntervalTitle)
                        else -> ""
                    }
                ),
                iconId = ObservableInt(interval.iconId),
                title = ObservableField(interval.name),
                description = ObservableField(
                    resources.string(
                        R.string.timerTimeFormat,
                        interval.time.hours(),
                        interval.time.minutes(),
                        interval.time.seconds()
                    )
                )
            )
        }
    }

    private fun updateTimeRemaining(timeRemaining: Int) {
        setTimeRemaining(timeRemaining)

        if (timer.shouldRepeat && (intervals.size <= timer.intervalCount / 2 || intervals.size == 1)) {
            addRepeatIntervals()
        }

        if (timeRemaining == 0) onIntervalFinished()
    }

    private fun setTimeRemaining(timeRemaining: Int) {
        currentIntervalTimeRemaining = timeRemaining.toFloat()
        screen.timeRemaining.set(
            resources.string(
                R.string.timerTimeFormat,
                hoursFromMilliseconds(timeRemaining.toLong()),
                minutesFromMilliseconds(timeRemaining.toLong()),
                secondsFromMilliseconds(timeRemaining.toLong())
            )
        )
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
        if (!isMuted) {
            resources.playSound(timer.intervalSound.id)
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