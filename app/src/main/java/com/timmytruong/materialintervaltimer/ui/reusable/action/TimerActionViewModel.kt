package com.timmytruong.materialintervaltimer.ui.reusable.action

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.data.TimerRepository
import com.timmytruong.data.di.BackgroundDispatcher
import com.timmytruong.data.di.MainDispatcher
import com.timmytruong.data.model.Timer
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.providers.DateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class TimerActionViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val directions: TimerActionDirections,
    private val dateProvider: DateProvider
) : BaseViewModel() {

    private lateinit var timer: Timer

    private val _favorite = MutableStateFlow(false)
    val favorite: Flow<Boolean> = _favorite

    fun fetchTimer(id: Int) = startSuspending(ioDispatcher) {
        timer = timerRepository.getTimerById(id)
        _favorite.value = timer.isFavorited
    }

    fun onFavoriteClicked() = startSuspending(ioDispatcher) {
        timerRepository.setFavorite(
            id = timer.id,
            favorite = !timer.isFavorited,
            currentDate = dateProvider.getCurrentDate()
        )
        Event.ToastMessage(
            when (!timer.isFavorited) {
                true -> R.string.favorited
                false -> R.string.unfavorited
            }
        ).fire()
        Event.BottomSheet.Dismiss.fire()
    }

    fun onDeleteClicked() = startSuspending(ioDispatcher) {
        timerRepository.deleteTimer(id = timer.id)
        Event.ToastMessage(R.string.deleted).fire()
        Event.BottomSheet.Dismiss.fire()
    }

    fun onStartClicked() = Event.Navigate(directions.toTimer(timer.id)).fire()
}