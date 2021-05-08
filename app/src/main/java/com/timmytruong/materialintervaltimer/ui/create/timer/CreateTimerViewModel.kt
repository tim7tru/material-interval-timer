package com.timmytruong.materialintervaltimer.ui.create.timer

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.DISMISS_EVENT
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalItemScreenBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalSoundScreenBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.views.IntervalSoundsBottomSheetScreen
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import com.timmytruong.materialintervaltimer.utils.currentDate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CreateTimerViewModel @Inject constructor(
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val timerStore: Store<Timer>,
    private val resources: ResourceProvider,
    private val screen: CreateTimerScreen,
    private val soundsBottomSheet: IntervalSoundsBottomSheetScreen,
    private val timerLocalDataSource: TimerRepository,
    private val sounds: List<IntervalSound>,
) : BaseViewModel() {

    private var soundBindings: List<IntervalSoundScreenBinding> =
        sounds.mapIndexed { position, sound ->
            IntervalSoundScreenBinding(
                soundName = ObservableField<String>(sound.name),
                isSelected = ObservableBoolean(sound.isSelected),
                position = position,
                clicks = ::onSoundClicked
            )
        }

    fun observe() = startSuspending(ioDispatcher) {
        timerStore.observe.collectLatest {
            screen.intervals = mapIntervals(it.intervals)
            screen.timerIntervalCount.set(it.intervalCount)
            screen.timerTitle.set(it.title)
            screen.timerSelectedSound.set(it.intervalSound.name)
            setSoundSelected { binding -> binding.soundName.get() == it.intervalSound.name }
            soundsBottomSheet.list = soundBindings
        }
        timerStore.refresh()
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
            it.createdDate = currentDate()
            it.updatedDate = currentDate()
            it.setTotalTime()
        }

        val id = timerLocalDataSource.saveNewTimer(timer = timerStore.get())
        navigateWith(screen.navToTimer(id.toInt()))
    }

    fun dismissSoundsBottomSheet() = fireEvents(DISMISS_EVENT)

    fun onGoToAddIntervalClicked() = navigateWith(screen.navToAddInterval())

    fun onGoToSoundsBottomSheet() = navigateWith(screen.navToSoundBottomSheet())

    private fun onSoundClicked(binding: IntervalSoundScreenBinding) = setSoundSelected {
        if (it == binding) {
            val position = it.position
            startSuspending(Dispatchers.IO) {
                timerStore.update { timer -> timer.intervalSound = sounds[position] }
            }
            resources.playSound(sounds[position].id)
            soundsBottomSheet.list = soundBindings
            true
        } else {
            false
        }
    }

    private fun mapIntervals(list: ArrayList<Interval>) = list.map {
        IntervalItemScreenBinding(
            iconId = ObservableInt(it.iconId),
            title = ObservableField(it.name),
            description = ObservableField(
                resources.string(
                    R.string.timeFormat,
                    it.time.hours(),
                    it.time.minutes(),
                    it.time.seconds()
                )
            )
        )
    }

    private fun setSoundSelected(predicate: (IntervalSoundScreenBinding) -> Boolean) {
        soundBindings.forEach { it.isSelected.set(predicate.invoke(it)) }
    }
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