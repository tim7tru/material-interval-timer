package com.timmytruong.materialintervaltimer.ui.create.timer

import android.content.Context
import android.media.MediaPlayer
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.DISMISS_EVENT
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.di.WeakContext
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalItemScreenBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalSoundScreenBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.views.IntervalSoundsBottomSheetScreen
import com.timmytruong.materialintervaltimer.utils.getCurrentDate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class CreateTimerViewModel @Inject constructor(
    @WeakContext private val ctx: WeakReference<Context>,
    @TimerStore private val timerStore: Store<Timer>,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val screen: CreateTimerScreen,
    private val soundsBottomSheet: IntervalSoundsBottomSheetScreen,
    private val timerLocalDataSource: TimerRepository,
    private val sounds: List<IntervalSound>,
) : BaseViewModel() {

    init {
        startSuspending(ioDispatcher) {
            timerStore.observe.collectLatest {
                screen.intervals = mapIntervals(it.intervals)
                screen.timerIntervalCount.set(it.intervalCount)
                screen.timerTitle.set(it.title)
                screen.timerSelectedSound.set(it.intervalSound.name)
                setSoundSelected { binding -> binding.soundName.get() == it.intervalSound.name }
                soundsBottomSheet.list = soundBindings
            }
        }
    }

    private var soundBindings: List<IntervalSoundScreenBinding> =
        sounds.mapIndexed { position, sound ->
            IntervalSoundScreenBinding(
                soundName = ObservableField<String>(sound.name),
                isSelected = ObservableBoolean(sound.isSelected),
                position = position,
                clicks = ::onSoundClicked
            )
        }

    fun fetchCurrentTimer() = startSuspending(ioDispatcher) { timerStore.forceEmit() }

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
            it.createdDate = getCurrentDate()
            it.updatedDate = getCurrentDate()
            it.setTotalTime()
        }

        saveTimer()
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
            if (sounds[position].id != -1) {
                MediaPlayer.create(ctx.get(), sounds[position].id).start()
            }
            soundsBottomSheet.list = soundBindings
            true
        } else {
            false
        }
    }

    private suspend fun saveTimer() {
        val id = timerLocalDataSource.saveNewTimer(timer = timerStore.get())
        navigateWith(screen.navToTimer(id.toInt()))
    }

    private fun mapIntervals(list: ArrayList<Interval>) = list.map {
        IntervalItemScreenBinding(
            iconId = ObservableInt(it.iconId),
            title = ObservableField(it.name),
            description = ObservableField(it.timeFormat)
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