package com.timmytruong.materialintervaltimer.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType

abstract class BaseViewModel: ViewModel() {
    private val _event = MutableLiveData<Event<Any>>()
    val event: LiveData<Event<Any>> get() = _event

    protected fun setEvent(event: Any) {
        _event.value = Event(event)
    }
}