package com.timmytruong.materialintervaltimer.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timmytruong.materialintervaltimer.utils.events.Event

abstract class BaseViewModel: ViewModel() {
    private val _event = MutableLiveData<Event<Pair<String, Any>>>()
    val event: LiveData<Event<Pair<String, Any>>> get() = _event

    protected fun setEvent(key: String, value: Any = Unit) {
        _event.value = Event(Pair(key, value))
    }
}