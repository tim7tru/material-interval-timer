package com.timmytruong.materialintervaltimer.ui.reusable.action

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@HiltViewModel
class TimerActionViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val screen: TimerActionBottomSheetScreen
) : BaseViewModel() {

    fun onFavouritedClicked(id: Int, favourite: Boolean) = startSuspending(ioDispatcher) {
        timerRepository.setFavourite(id = id, favourite = favourite)
        fireEvents(
            FAVOURITE to when (favourite) {
                true -> R.string.favourited
                false -> R.string.unfavourited
            }
        )
    }

    fun onDeleteClicked(id: Int) = startSuspending(ioDispatcher) {
        timerRepository.deleteTimer(id = id)
        fireEvents(DELETE to R.string.deleted)
    }

    fun onStartClicked(id: Int) = navigateWith(screen.navToTimer(id))
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class TimerActionViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideTimerActionBottomSheetScreen() = TimerActionBottomSheetScreen()
}