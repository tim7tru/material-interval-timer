package com.timmytruong.materialintervaltimer.ui.create.interval.time

import android.content.Context
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.IntervalStore
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.formatInputtedTime
import com.timmytruong.materialintervaltimer.utils.formatNormalizedTime
import com.timmytruong.materialintervaltimer.utils.getSecondsFromTime
import com.timmytruong.materialintervaltimer.utils.string
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CreateIntervalTimeViewModel @Inject constructor(
    @ApplicationContext private val ctx: Context,
    @TimerStore private val timerStore: Store<Timer>,
    @IntervalStore private val intervalStore: Store<Interval>,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val screen: CreateIntervalTimeScreen
) : BaseViewModel() {

    init {
        startSuspending(ioDispatcher) {
            intervalStore.observe.collectLatest {
                screen.intervalTimeLengthValidity.set(it.interval_time_format.isNotBlank())
                screen.intervalDisplayTime.set(
                    formatInputtedTime(
                        it.interval_time_format,
                        ctx.string(R.string.timeFormat)
                    )
                )
            }
        }
    }

    fun fetchTime() = startSuspending(ioDispatcher) { intervalStore.forceEmit() }

    fun addToTime(newNumber: String) = startSuspending(ioDispatcher) {
        val curTime = intervalStore.get().interval_time_format
        if (curTime.length < 6) {
            intervalStore.update { it.interval_time_format = "${curTime}${newNumber}" }
        }
    }

    fun removeFromTime() = startSuspending(ioDispatcher) {
        val curTime = intervalStore.get().interval_time_format
        if (curTime.isNotEmpty()) {
            intervalStore.update { it.interval_time_format = curTime.dropLast(1) }
        }
    }

    fun addInterval() = startSuspending(ioDispatcher) {
        intervalStore.update {
            it.interval_time_ms = getSecondsFromTime(time = it.interval_time_format) * 1000
            it.interval_time_format = formatNormalizedTime(
                time = it.interval_time_format,
                format = ctx.string(R.string.timeFormat)
            )
        }

        timerStore.update {
            it.timer_intervals.add(intervalStore.get().copy())
            it.timer_intervals_count = it.timer_intervals.size
        }

        navigateWith(screen.navBackToCreateTimer())
    }
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class CreateIntervalTimeModule {

    @ActivityRetainedScoped
    @Provides
    fun provideScreen() = CreateIntervalTimeScreen()
}