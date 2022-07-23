package com.timmytruong.materialintervaltimer.ui.create.timer.sounds

import com.timmytruong.data.di.BackgroundDispatcher
import com.timmytruong.data.di.MainDispatcher
import com.timmytruong.data.di.TimerStore
import com.timmytruong.data.local.Store
import com.timmytruong.data.model.IntervalSound
import com.timmytruong.data.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.reusable.item.IntervalSoundItem
import com.timmytruong.materialintervaltimer.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class IntervalSoundsViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @TimerStore private val timerStore: Store<Timer>,
    private val sounds: List<IntervalSound>
) : BaseViewModel() {

    private val soundItems: List<IntervalSoundItem> = sounds.map { it.toListItem() }

    private val _soundsFlow = MutableSharedFlow<List<IntervalSoundItem>>()
    val soundsFlow: Flow<List<IntervalSoundItem>> = _soundsFlow

    fun fetchSounds(soundId: Int) {
        setSoundSelected { soundItems[it].id == soundId }
    }

    fun dismissSoundsBottomSheet() = Event.BottomSheet.Dismiss.fire()

    private fun onSoundClicked(position: Int) = setSoundSelected {
        if (it == position) {
            startSuspending(ioDispatcher) {
                timerStore.update { timer -> timer.intervalSound = sounds[position] }
                if (sounds[position].id != -1) Event.PlaySound(sounds[position].id).fire()
            }
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