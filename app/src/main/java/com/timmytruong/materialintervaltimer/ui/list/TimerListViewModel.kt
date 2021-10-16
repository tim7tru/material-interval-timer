package com.timmytruong.materialintervaltimer.ui.list

import androidx.databinding.ObservableBoolean
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.TimerListScreenBinding
import com.timmytruong.materialintervaltimer.utils.ObservableString
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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TimerListViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val screen: TimerListScreen,
    private val resources: ResourceProvider
) : BaseViewModel() {

    fun fetchTimers() = startSuspending(ioDispatcher) {
        screen.timers = when (screen.name) {
            FavouritesFragment::class.java.simpleName -> timerRepository.getFavouritedTimers()
                .map { it.toBindings() }
            RecentsFragment::class.java.simpleName -> timerRepository.getRecentTimers()
                .map { it.toBindings() }
            else -> error("fragment type not found")
        }
    }

    private fun List<Timer>.toBindings(): List<TimerListScreenBinding> {
        return map { timer ->
            TimerListScreenBinding(
                time = ObservableString(timer.totalTimeMs.toDisplayTime(resources)),
                title = ObservableString(timer.title),
                intervalCount = ObservableString(resources.string(R.string.number_of_intervals_format, timer.intervalCount)),
                timerId = timer.id,
                clicks = { navigateWith(screen.navToBottomSheet(timer.id)) }
            )
        }
    }
}

data class TimerListScreen(
    val isEmpty: ObservableBoolean = ObservableBoolean(false),
    var timers: Flow<@JvmSuppressWildcards List<TimerListScreenBinding>> = emptyFlow()
) : BaseScreen() {

    fun navToBottomSheet(id: Int) = when (name) {
        FavouritesFragment::class.java.simpleName -> FavouritesFragmentDirections.actionFavouritesFragmentToTimerActionBottomSheet(timerId = id)
        RecentsFragment::class.java.simpleName -> RecentsFragmentDirections.actionRecentsFragmentToTimerActionBottomSheet(timerId = id)
        else -> error("fragment type not found")
    }
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class TimerListViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideTimerListScreen() = TimerListScreen()

}