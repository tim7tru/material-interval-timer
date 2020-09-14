package com.timmytruong.materialintervaltimer.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType

abstract class BaseViewModel: ViewModel() {
    val error = MutableLiveData<ErrorType>()

    fun setError(errorType: ErrorType) {
        error.value = errorType
    }
}