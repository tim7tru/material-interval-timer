package com.timmytruong.materialintervaltimer.ui.create.interval.time

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.Time
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CreateIntervalTimeViewModel @Inject constructor(
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val resources: ResourceProvider,
    private val timerStore: Store<Timer>,
    private val intervalStore: Store<Interval>,
    private val timeStore: Store<Time>,
    private val screen: CreateIntervalTimeScreen
) : BaseViewModel() {

    fun observe() = startSuspending(ioDispatcher) {
        timeStore.observe.collectLatest {
            screen.intervalTimeLengthValidity.set(it.isInputValid())
            screen.intervalDisplayTime.set(
                resources.string(
                    R.string.timeFormat,
                    it.hours(normalized = false),
                    it.minutes(normalized = false),
                    it.seconds(normalized = false)
                )
            )
        }
        timeStore.refresh()
    }

    fun addToTime(newNumber: String) = startSuspending(ioDispatcher) {
        timeStore.update { it.addToInput(newNumber) }
    }

    fun removeFromTime() = startSuspending(ioDispatcher) {
        timeStore.update { it.removeFromInput() }
    }

    fun addInterval() = startSuspending(ioDispatcher) {
        timeStore.update { it.finalize() }

        intervalStore.update { it.time = timeStore.get().copy() }

        timerStore.update {
            it.intervals.add(intervalStore.get().copy())
            it.intervalCount = it.intervals.size
        }

        timeStore.update { it.clear() }
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