package com.timmytruong.materialintervaltimer.ui.create.timer

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.TimerStore
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.IntervalItem
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.toListItems
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.providers.DateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CreateTimerViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @TimerStore private val timerStore: Store<Timer>,
    private val timerLocalDataSource: TimerRepository,
    private val date: DateProvider,
    private val directions: CreateTimerDirections
) : BaseViewModel() {

    private val _intervals = MutableStateFlow<List<IntervalItem>>(listOf())
    val intervals: Flow<List<IntervalItem>> = _intervals

    private val _title = MutableStateFlow("")
    val title: Flow<String> = _title

    private val _sound = MutableStateFlow("None")
    val sound: Flow<String> = _sound

    private val _shouldRepeat = MutableStateFlow(false)
    val shouldRepeat: Flow<Boolean> = _shouldRepeat

    private val _isSaved = MutableStateFlow(false)
    val isSaved: Flow<Boolean> = _isSaved

    fun fetchCurrentTimer(clearTimer: Boolean) = startSuspending(ioDispatcher) { scope ->
        timerStore.observe.onEach {
            _intervals.value = it.intervals.toListItems(hasHeaders = false)
            _title.value = it.title
            _sound.value = it.intervalSound.name
        }.launchIn(scope)

        when (clearTimer) {
            true -> timerStore.update { it.clear() }
            false -> timerStore.refresh()
        }
    }

    fun setTimerTitle(title: String) = startSuspending(ioDispatcher) {
        timerStore.update { it.title = title }
    }

    fun setRepeat(checked: Boolean) = startSuspending(ioDispatcher) {
        timerStore.update { it.shouldRepeat = checked }
        _shouldRepeat.value = checked
    }

    fun setFavorite(checked: Boolean) = startSuspending(ioDispatcher) {
        timerStore.update { it.isFavorited = checked }
        _isSaved.value = checked
    }

    fun validateTimer(title: String) = startSuspending(ioDispatcher) {
        val currentDate = date.getCurrentDate()

        timerStore.update {
            it.title = title
            it.createdDate = currentDate
            it.updatedDate = currentDate
            it.setTotalTime()
        }

        val id = timerLocalDataSource.saveNewTimer(timer = timerStore.get())
        Event.Navigate(directions.toTimer(id.toInt())).fire()
    }

    fun onGoToAddIntervalClicked() = Event.Navigate(directions.toCreateInterval()).fire()

    fun onGoToSoundsBottomSheet() =
        Event.Navigate(directions.toSounds(timerStore.get().intervalSound.id)).fire()
}