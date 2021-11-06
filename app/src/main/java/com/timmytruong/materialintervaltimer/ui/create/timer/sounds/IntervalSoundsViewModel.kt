package com.timmytruong.materialintervaltimer.ui.create.timer.sounds

import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class IntervalSoundsViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @TimerStore private val timerStore: Store<Timer>,
    private val sounds: List<IntervalSound>,
    private val soundsBottomSheet: IntervalSoundsBottomSheetScreen,
    private val resources: ResourceProvider
) : BaseViewModel() {

    private val soundItems: List<IntervalSoundItem> = sounds.map { it.toListItem() }

    private val _soundsFlow = MutableSharedFlow<List<IntervalSoundItem>>()

    fun fetchSounds(soundId: Int) {
        soundsBottomSheet.list = _soundsFlow
        setSoundSelected { soundItems[it].id == soundId }
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

    private fun setSoundSelected(predicate: (Int) -> Boolean) = startSuspending(mainDispatcher) {
        soundItems.forEachIndexed { idx, sound ->
            sound.isSelected = predicate.invoke(idx)
        }
        _soundsFlow.emit(soundItems)
    }

    private fun IntervalSound.toListItem() = IntervalSoundItem(
        id = id,
        title = name,
        isSelected = isSelected,
        clicks = { onSoundClicked(it) }
    )
}