package com.timmytruong.materialintervaltimer.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timmytruong.materialintervaltimer.utils.events.Event
import javax.inject.Inject

const val NAVIGATE = "navigate"

abstract class BaseViewModel : ViewModel() {
    private val eventItem = Event<Pair<String, Any>>()

    private val _event = MutableLiveData<Event<Pair<String, Any>>>()
    val event: LiveData<Event<Pair<String, Any>>> get() = _event

    protected fun setEvent(key: String, value: Any = Unit) {
        eventItem.value = Pair(key, value)
        _event.value = eventItem
    }
}