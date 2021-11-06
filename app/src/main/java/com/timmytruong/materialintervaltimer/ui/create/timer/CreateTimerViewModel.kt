package com.timmytruong.materialintervaltimer.ui.create.timer

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.create.timer.sounds.IntervalSoundsBottomSheetScreen
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.toListItems
import com.timmytruong.materialintervaltimer.utils.providers.DateProvider
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
class CreateTimerViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @TimerStore private val timerStore: Store<Timer>,
    private val screen: CreateTimerScreen,
    private val timerLocalDataSource: TimerRepository,
    private val resources: ResourceProvider,
    private val date: DateProvider
) : BaseViewModel() {

    fun fetchCurrentTimer(clearTimer: Boolean) = startSuspending(ioDispatcher) {
        timerStore.observe.collectLatest {
            screen.intervals = it.intervals.toListItems(resources, hasHeaders = false)
            screen.timerIntervalCount.set(it.intervalCount)
            screen.timerTitle.set(it.title)
            screen.timerSelectedSound.set(it.intervalSound.name)
        }

        when (clearTimer) {
            true -> timerStore.update { it.clear() }
            false -> timerStore.refresh()
        }
    }

    fun clearTimer() = startSuspending(ioDispatcher) { timerStore.update { it.clear() } }

    fun setTimerTitle(title: String) = startSuspending(ioDispatcher) {
        timerStore.update { it.title = title }
    }

    fun setRepeat(checked: Boolean) = startSuspending(ioDispatcher) {
        timerStore.update { it.shouldRepeat = checked }
        screen.timerIsRepeated.set(checked)
    }

    fun setFavourite(checked: Boolean) = startSuspending(ioDispatcher) {
        timerStore.update { it.isFavourited = checked }
        screen.timerIsSaved.set(checked)
    }

    fun validateTimer(title: String) = startSuspending(ioDispatcher) {
        timerStore.update {
            it.title = title
            it.createdDate = date.getCurrentDate()
            it.updatedDate = date.getCurrentDate()
            it.setTotalTime()
        }

        val id = timerLocalDataSource.saveNewTimer(timer = timerStore.get())
        navigateWith(screen.navToTimer(id.toInt()))
    }

    fun onGoToAddIntervalClicked() = navigateWith(screen.navToAddInterval())

    fun onGoToSoundsBottomSheet() =
        navigateWith(screen.navToSoundBottomSheet(timerStore.get().intervalSound.id))
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class CreateTimerViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideScreen(): CreateTimerScreen = CreateTimerScreen()


    @ActivityRetainedScoped
    @Provides
    fun provideIntervalSoundsBottomSheetScreen(): IntervalSoundsBottomSheetScreen =
        IntervalSoundsBottomSheetScreen()
}