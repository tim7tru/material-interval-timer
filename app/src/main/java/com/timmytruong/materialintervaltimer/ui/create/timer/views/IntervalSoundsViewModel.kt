package com.timmytruong.materialintervaltimer.ui.create.timer.views

import androidx.databinding.ObservableBoolean
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalSoundScreenBinding
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.ObservableString
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class IntervalSoundsViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @TimerStore private val timerStore: Store<Timer>,
    private val sounds: List<IntervalSound>,
    private val soundsBottomSheet: IntervalSoundsBottomSheetScreen,
    private val resources: ResourceProvider
): BaseViewModel() {

    private val soundBindings: List<IntervalSoundScreenBinding> = sounds.map { it.toBinding() }

    fun fetchSounds(soundId: Int) {
        setSoundSelected { soundBindings[it].id == soundId }
        soundsBottomSheet.list = soundBindings
    }

    fun dismissSoundsBottomSheet() = fireEvent(Event.BottomSheet.Dismiss)

    private fun onSoundClicked(position: Int) = setSoundSelected {
        if (it == position) {
            startSuspending(ioDispatcher) {
                timerStore.update { timer -> timer.intervalSound = sounds[position] }
            }
            if (sounds[position].id != -1) { fireEvent(Event.PlaySound(sounds[position].id)) }
            true
        } else {
            false
        }
    }

    private fun setSoundSelected(predicate: (Int) -> Boolean) {
        soundBindings.forEachIndexed { idx, sound -> sound.isSelected.set(predicate.invoke(idx)) }
    }

    private fun IntervalSound.toBinding() = IntervalSoundScreenBinding(
        id = id,
        soundName = ObservableString(name),
        isSelected = ObservableBoolean(isSelected),
        clicks = { onSoundClicked(it) }
    )
}