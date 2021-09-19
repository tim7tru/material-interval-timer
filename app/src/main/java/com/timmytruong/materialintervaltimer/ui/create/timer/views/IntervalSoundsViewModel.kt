package com.timmytruong.materialintervaltimer.ui.create.timer.views

import android.media.MediaPlayer
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.DISMISS_EVENT
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalSoundScreenBinding
import com.timmytruong.materialintervaltimer.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class IntervalSoundsViewModel @Inject constructor(
    @TimerStore private val timerStore: Store<Timer>,
    private val sounds: List<IntervalSound>,
    private val soundsBottomSheet: IntervalSoundsBottomSheetScreen,
    private val resources: ResourceProvider
): BaseViewModel() {

    private var soundBindings: List<IntervalSoundScreenBinding> =
        sounds.mapIndexed { position, sound ->
            IntervalSoundScreenBinding(
                soundName = ObservableField<String>(sound.name),
                isSelected = ObservableBoolean(sound.isSelected),
                position = position,
                clicks = ::onSoundClicked
            )
        }

    init { soundsBottomSheet.list = soundBindings }

    fun observeStore() = startSuspending(ioDispatcher) {
        timerStore.observe.collectLatest {
            setSoundSelected { binding -> binding.soundName.get() == it.intervalSound.name }
        }
    }

    fun dismissSoundsBottomSheet() = fireEvents(DISMISS_EVENT)

    private fun onSoundClicked(binding: IntervalSoundScreenBinding) = setSoundSelected {
        if (it == binding) {
            val position = it.position
            startSuspending(ioDispatcher) {
                timerStore.update { timer -> timer.intervalSound = sounds[position] }
            }
            if (sounds[position].id != -1) {
                MediaPlayer.create(resources.ctx, sounds[position].id).start()
            }
            soundsBottomSheet.list = soundBindings
            true
        } else {
            false
        }
    }

    private fun setSoundSelected(predicate: (IntervalSoundScreenBinding) -> Boolean) {
        soundBindings.forEach { it.isSelected.set(predicate.invoke(it)) }
    }
}