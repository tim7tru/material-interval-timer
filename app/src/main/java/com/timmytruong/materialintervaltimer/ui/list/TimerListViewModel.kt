package com.timmytruong.materialintervaltimer.ui.list

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
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
class TimerListViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val screen: TimerListScreen,
    private val resources: ResourceProvider
) : BaseViewModel() {

    fun fetchTimers(type: TimerType) = startSuspending(ioDispatcher) {
        screen.timers = when (type) {
            TimerType.FAVOURITES -> timerRepository.getFavouritedTimers().toTimerItems()
            TimerType.RECENTS -> timerRepository.getRecentTimers().toTimerItems()
        }
    }

    private fun Flow<List<Timer>>.toTimerItems() = map { it.toTimerItems(resources, ::onTimerClicked)}

    private fun onTimerClicked(id: Int, favourited: Boolean) = navigateWith(screen.navToBottomSheet(id, favourited))
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class TimerListViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideTimerListScreen() = TimerListScreen()

}