package com.timmytruong.materialintervaltimer.ui.create.timer.views

import android.media.MediaPlayer
import androidx.databinding.ObservableBoolean
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalSoundScreenBinding
import com.timmytruong.materialintervaltimer.utils.ObservableString
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
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

    private val _bindings = MutableSharedFlow<List<IntervalSoundScreenBinding>>()

    init {
        soundsBottomSheet.list = _bindings
        startSuspending(ioDispatcher) {
            timerStore.observe.collectLatest { timer ->
                setSoundSelected { soundBindings[it].id == timer.intervalSound.id }
                _bindings.emit(soundBindings)
            }
        }
    }

    fun dismissSoundsBottomSheet() = navigateWith(soundsBottomSheet.navToCreateTimer())

    private fun onSoundClicked(position: Int) = setSoundSelected {
        if (it == position) {
            startSuspending(ioDispatcher) {
                timerStore.update { timer -> timer.intervalSound = sounds[position] }
            }
            if (sounds[position].id != -1) {
                MediaPlayer.create(resources.ctx, sounds[position].id).start()
            }
            true
        } else {
            false
        }
    }

    private fun setSoundSelected(predicate: (Int) -> Boolean) {
        soundBindings.forEach { if (it.position != -1) it.isSelected.set(predicate.invoke(it.position)) }
    }

    private fun IntervalSound.toBinding() = IntervalSoundScreenBinding(
        id = id,
        soundName = ObservableString(name),
        isSelected = ObservableBoolean(isSelected),
        clicks = { onSoundClicked(it) }
    )
}