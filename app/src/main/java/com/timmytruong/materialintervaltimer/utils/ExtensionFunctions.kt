package com.timmytruong.materialintervaltimer.utils

import androidx.lifecycle.MutableLiveData

object ExtensionFunctions {
    fun <T> MutableLiveData<T>.forceRefresh() {
        this.value = this.value
    }
}