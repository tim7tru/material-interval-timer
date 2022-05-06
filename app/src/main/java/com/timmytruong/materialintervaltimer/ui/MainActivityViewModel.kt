package com.timmytruong.materialintervaltimer.ui

import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.home.HomeDirections
import com.timmytruong.materialintervaltimer.ui.list.TimerType
import com.timmytruong.materialintervaltimer.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val directions: HomeDirections
) : BaseViewModel() {

    fun navToCreateTimer() = Event.Navigate(directions.toCreateTimer()).fire()

    fun navToRecents() = Event.Navigate(directions.toTimerList(TimerType.RECENTS)).fire()

    fun navToFavorites() = Event.Navigate(directions.toTimerList(TimerType.FAVORITES)).fire()
}