package com.timmytruong.materialintervaltimer.base

import androidx.navigation.NavDirections
import kotlinx.coroutines.Job

interface BaseObserver<V: BaseViewModel> {
    val viewModel: V

    val uiStateJobs: ArrayList<Job>

    fun eventHandler(event: Pair<String, Any>)

    fun navigationHandler(action: NavDirections): Unit? = null
}