package com.timmytruong.materialintervaltimer.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType

abstract class BaseViewModel: ViewModel() {
    private val _error = MutableLiveData<Event<ErrorType>>()
    val error: LiveData<Event<ErrorType>> get() = _error

    fun setError(errorType: ErrorType) {
        _error.value = Event(errorType)
    }
}