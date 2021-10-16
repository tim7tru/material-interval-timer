package com.timmytruong.materialintervaltimer.ui.create.interval.time

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.IntervalStore
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_HOUR_L
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_MINS_L
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_SECS_L
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class CreateIntervalTimeViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val resources: ResourceProvider,
    @TimerStore private val timerStore: Store<Timer>,
    @IntervalStore private val intervalStore: Store<Interval>,
    private val screen: CreateIntervalTimeScreen
) : BaseViewModel() {

    private var time: String = ""
        set(value) {
            field = value
            screen.intervalTimeLengthValidity.set(field.isNotEmpty())
        }

    init {
        screen.intervalDisplayTime.set(getDisplayTime())
    }

    fun addToTime(newNumber: String) {
        if (time.length <= 6) {
            time = "$time$newNumber"
            screen.intervalDisplayTime.set(getDisplayTime())
        }
    }

    fun removeFromTime() {
        if (time.isNotEmpty()) {
            time = time.dropLast(1)
            screen.intervalDisplayTime.set(getDisplayTime())
        }
    }

    fun addInterval() = startSuspending(ioDispatcher) {
        intervalStore.update { it.timeMs = getTimeMs() }

        timerStore.update {
            it.intervals.add(intervalStore.get().copy())
            it.intervalCount = it.intervals.size
        }

        navigateWith(screen.navToCreateTimer())
    }

    fun backPressed() = navigateWith(screen.navToCreateInterval())

    private fun getDisplayTime(): String {
        val temp = fillTime()
        return resources.string(
            R.string.inputTimeFormat,
            temp.subSequence(0, 2),
            temp.subSequence(2, 4),
            temp.subSequence(4, 6)
        )
    }

    private fun fillTime(): String {
        var temp = time
        return when (temp.length >= 6) {
            true -> temp
            false -> {
                while (temp.length < 6) {
                    temp = "0${temp}"
                }
                temp
            }
        }
    }

    private fun getTimeMs(): Long {
        val temp = if (time.length < 6) fillTime() else time
        var milliseconds = 0L
        milliseconds += temp.subSequence(4, 6).toString().toLong() * MILLI_IN_SECS_L
        milliseconds += temp.subSequence(2, 4).toString().toLong() * MILLI_IN_MINS_L
        milliseconds += temp.subSequence(0, 2).toString().toLong() * MILLI_IN_HOUR_L
        return milliseconds
    }
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class CreateIntervalTimeModule {

    @ActivityRetainedScoped
    @Provides
    fun provideScreen() = CreateIntervalTimeScreen()
}