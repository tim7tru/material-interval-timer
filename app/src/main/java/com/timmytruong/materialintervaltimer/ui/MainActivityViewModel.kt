package com.timmytruong.materialintervaltimer.ui

import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.home.HomeDirections
import com.timmytruong.materialintervaltimer.ui.list.TimerType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val directions: HomeDirections
) : BaseViewModel() {

    fun navToCreateTimer() = navigateWith(directions.toCreateTimer())

    fun navToRecents() = navigateWith(directions.toTimerList(TimerType.RECENTS))

    fun navToFavorites() = navigateWith(directions.toTimerList(TimerType.FAVORITES))
}