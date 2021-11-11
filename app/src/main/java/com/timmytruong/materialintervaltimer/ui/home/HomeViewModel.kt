package com.timmytruong.materialintervaltimer.ui.home

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.list.TimerType
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.toTimerItems
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val screen: HomeScreen,
    private val resources: ResourceProvider
) : BaseViewModel() {

    companion object {
        private const val NUM_TIMERS_SHOWN = 7
    }

    fun fetchRecentTimers() = startSuspending(ioDispatcher) {
        screen.recents = timerRepository.getRecentTimers().toTimerItems()
    }

    fun fetchFavouriteTimers() = startSuspending(ioDispatcher) {
        screen.favourites = timerRepository.getFavouritedTimers().toTimerItems()
    }

    fun onAddClicked() = navigateWith(screen.navToCreateTimer())

    fun onFavouritesSeeAllClicked() = navigateWith(screen.navToTimerList(TimerType.FAVOURITES))

    fun onRecentsSeeAllClicked() = navigateWith(screen.navToTimerList(TimerType.RECENTS))

    private fun Flow<List<Timer>>.toTimerItems() = map { it.trim().toTimerItems(resources, ::onTimerClicked) }

    private fun onTimerClicked(id: Int, favourited: Boolean) = navigateWith(screen.navToBottomSheet(id, favourited))

    private fun List<Timer>.trim() = when {
        size < NUM_TIMERS_SHOWN -> subList(0, size)
        else -> subList(0, NUM_TIMERS_SHOWN)
    }
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class HomeViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideHomeScreen() = HomeScreen()
}