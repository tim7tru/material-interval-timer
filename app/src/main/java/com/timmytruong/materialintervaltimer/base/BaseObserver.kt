package com.timmytruong.materialintervaltimer.base

import androidx.lifecycle.Observer
import com.timmytruong.materialintervaltimer.utils.events.Event

interface BaseObserver {
    val baseViewModel: BaseViewModel

    val eventObserver: Observer<Event<Pair<String, Any>>>

    fun subscribeObservers()

    fun isEventHandled(event: Event<Pair<String, Any>>) = event.getContentIfNotHandled()
}