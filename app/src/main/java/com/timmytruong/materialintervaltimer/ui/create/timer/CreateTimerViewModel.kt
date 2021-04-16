package com.timmytruong.materialintervaltimer.ui.create.timer

import android.content.Context
import android.media.MediaPlayer
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.DISMISS
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalItemScreenBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalSoundScreenBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.views.IntervalSoundsBottomSheetScreen
import com.timmytruong.materialintervaltimer.utils.*
import com.timmytruong.materialintervaltimer.utils.events.INPUT_ERROR
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CreateTimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
                screen.intervals = mapIntervals(it.timer_intervals)
                screen.timerIntervalCount.set(it.timer_intervals_count)
                screen.timerTitle.set(it.timer_title)
                screen.timerSelectedSound.set(it.timer_interval_sound.sound_name)
                setSoundSelected { binding -> binding.soundName.get() == it.timer_interval_sound.sound_name }
                soundsBottomSheet.list = soundBindings
            }
        }
    }

    private var soundBindings: List<IntervalSoundScreenBinding> =
        sounds.mapIndexed { position, sound ->
            IntervalSoundScreenBinding(
                soundName = ObservableField<String>(sound.sound_name),
                isSelected = ObservableBoolean(sound.sound_is_selected),
                position = position,
                clicks = ::onSoundClicked
            )
        }

    fun fetchCurrentTimer() = startSuspending(ioDispatcher) { timerStore.forceEmit() }

    fun clearTimer() = startSuspending(ioDispatcher) { timerStore.update { it.clear() } }

    fun setTimerTitle(title: String) = startSuspending(ioDispatcher) {
        timerStore.update { it.timer_title = title }
    }

    fun setRepeat(checked: Boolean) = startSuspending(ioDispatcher) {
        timerStore.update { it.timer_repeat = checked }
        screen.timerIsRepeated.set(checked)
    }

    fun setSaved(checked: Boolean) = startSuspending(ioDispatcher) {
        timerStore.update { it.timer_saved = checked }
        screen.timerIsSaved.set(checked)
    }

    fun validateTimer(title: String) = startSuspending(ioDispatcher) {
        if (timerStore.get().timer_intervals.isNullOrEmpty()) {
            fireEvent(INPUT_ERROR)
            return@startSuspending
        }

        timerStore.update {
            it.timer_title = title
            it.timer_created_date = getCurrentDate()
            it.timer_updated_date = getCurrentDate()
            it.setTotalTime()
        }

        saveTimer()
    }

    fun dismissSoundsBottomSheet() = fireEvent(DISMISS)

    fun onGoToAddIntervalClicked() = navigateWith(screen.navToAddInterval())

    fun onGoToSoundsBottomSheet() = navigateWith(screen.navToSoundBottomSheet())

    private fun onSoundClicked(binding: IntervalSoundScreenBinding) = setSoundSelected {
        if (it == binding) {
            val position = it.position
            startSuspending(Dispatchers.IO) {
                timerStore.update { timer ->
                    timer.timer_interval_sound = sounds[position]
                }
            }
            if (sounds[position].sound_id != -1) {
                MediaPlayer.create(context, sounds[position].sound_id).start()
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
            iconId = ObservableInt(it.interval_icon_id),
            title = ObservableField(it.interval_name),
            description = ObservableField(it.interval_time_format)
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