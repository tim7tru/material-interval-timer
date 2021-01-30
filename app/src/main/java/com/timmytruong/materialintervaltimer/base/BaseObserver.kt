package com.timmytruong.materialintervaltimer.base

import androidx.lifecycle.Observer
import com.timmytruong.materialintervaltimer.utils.events.Event

interface BaseObserver {
    val baseViewModel: BaseViewModel

    val eventObserver: Observer<Event<Any>>

    fun subscribeObservers()
}