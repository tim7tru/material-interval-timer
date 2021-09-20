package com.timmytruong.materialintervaltimer.ui.home

import androidx.databinding.ObservableField
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.TimerListScreenBinding
import com.timmytruong.materialintervaltimer.ui.reusable.action.TimerActionBottomSheetScreen
import com.timmytruong.materialintervaltimer.utils.ResourceProvider
import com.timmytruong.materialintervaltimer.utils.toDisplayTime
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val NUM_TIMERS_SHOWN = 7

@HiltViewModel
class HomeViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val screen: HomeScreen,
    private val bottomSheet: TimerActionBottomSheetScreen,
    private val resources: ResourceProvider
) : BaseViewModel() {

    private val _recents = MutableSharedFlow<List<TimerListScreenBinding>>()
    val recents: Flow<List<TimerListScreenBinding>> = _recents

    private val _favourites = MutableSharedFlow<List<TimerListScreenBinding>>()
    val favourites: Flow<List<TimerListScreenBinding>> = _favourites

    fun fetchRecentTimers() = startSuspending(ioDispatcher) {
        timerRepository.getRecentTimers().map {
            val list = if (it.size < NUM_TIMERS_SHOWN) it.subList(0, it.size) else it
            list.map(::mapTimerToBinding)
        }.collect {
            _recents.emit(it)
        }
    }

    fun fetchFavouriteTimers() = startSuspending(ioDispatcher) {
        timerRepository.getFavouritedTimers().map {
            val list = if (it.size < NUM_TIMERS_SHOWN) it.subList(0, it.size) else it
            list.map(::mapTimerToBinding)
        }.collect {
            _favourites.emit(it)
        }
    }

    fun onAddClicked() = navigateWith(screen.navToCreateTimer())

    private fun mapTimerToBinding(timer: Timer) = TimerListScreenBinding(
        time = ObservableField(timer.totalTimeMs.toDisplayTime(resources)),
        title = ObservableField(timer.title),
        intervalCount = ObservableField(resources.string(R.string.number_of_intervals_format, timer.intervalCount)),
        timerId = timer.id,
        clicks = {
            startSuspending(ioDispatcher) {
                bottomSheet.timerId.set(timer.id)
                bottomSheet.isFavourite.set(timer.isFavourited)
                navigateWith(screen.navToBottomSheet())
            }
        }
    )
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class HomeViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideHomeScreen() = HomeScreen()
}