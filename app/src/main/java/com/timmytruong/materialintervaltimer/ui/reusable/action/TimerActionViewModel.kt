package com.timmytruong.materialintervaltimer.ui.reusable.action

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.Event
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class TimerActionViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val screen: TimerActionBottomSheetScreen
) : BaseViewModel() {

    private lateinit var timer: Timer

    fun fetchTimer(id: Int) = startSuspending {
        timer = timerRepository.getTimerById(id)
    }

    fun onFavouritedClicked() = startSuspending(ioDispatcher) {
        timerRepository.setFavourite(id = timer.id, favourite = timer.isFavourited)
        fireEvent(
            Event.BottomSheet.TimerAction.ToastMessage(
                when (timer.isFavourited) {
                    true -> R.string.favourited
                    false -> R.string.unfavourited
                }
            )
        )
    }

    fun onDeleteClicked() = startSuspending(ioDispatcher) {
        timerRepository.deleteTimer(id = timer.id)
        fireEvent(Event.BottomSheet.TimerAction.ToastMessage(R.string.deleted))
    }

    fun onStartClicked() = navigateWith(screen.navToTimer(timer.id))
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class TimerActionViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideTimerActionBottomSheetScreen() = TimerActionBottomSheetScreen()
}