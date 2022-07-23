package com.timmytruong.materialintervaltimer.ui.create.timer

import com.timmytruong.data.TimerRepository
import com.timmytruong.data.di.BackgroundDispatcher
import com.timmytruong.data.di.MainDispatcher
import com.timmytruong.data.di.TimerStore
import com.timmytruong.data.local.Store
import com.timmytruong.data.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.reusable.item.IntervalItem
import com.timmytruong.materialintervaltimer.ui.reusable.item.toListItem
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.providers.DateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        timerStore.observe.onEach { timer ->
            _intervals.value = timer.intervals.map { it.toListItem(hasHeaders = false) }
            _title.value = timer.title
            _sound.value = timer.intervalSound.name
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