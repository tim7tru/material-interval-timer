package com.timmytruong.materialintervaltimer.ui.base

import androidx.navigation.NavDirections
import com.timmytruong.materialintervaltimer.utils.Event
import kotlinx.coroutines.Job

interface BaseObserver<V: BaseViewModel> {
    val viewModel: V

    val uiStateJobs: ArrayList<Job>

    fun eventHandler(event: Event) {
        throw Error("Unknown event left unhandled: $event")
    }

    fun navigationHandler(action: NavDirections): Unit?
}