package com.timmytruong.materialintervaltimer.base

import androidx.navigation.NavDirections
import kotlinx.coroutines.Job

interface BaseObserver<V: BaseViewModel> {
    val viewModel: V

    val uiStateJobs: ArrayList<Job>

    val eventFlowHandler: suspend (Pair<String, Any>) -> Unit

    fun navigationHandler(action: NavDirections): Unit? = null
}