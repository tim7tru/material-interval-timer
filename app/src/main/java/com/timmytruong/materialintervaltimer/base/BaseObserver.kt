package com.timmytruong.materialintervaltimer.base

import androidx.navigation.NavDirections
import com.timmytruong.materialintervaltimer.utils.Event
import kotlinx.coroutines.Job

interface BaseObserver<V: BaseViewModel> {
    val viewModel: V

    val uiStateJobs: ArrayList<Job>

    fun eventHandler(event: Event)

    fun navigationHandler(action: NavDirections): Unit? = null
}