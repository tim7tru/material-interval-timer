package com.timmytruong.materialintervaltimer.utils

import androidx.annotation.StringRes

sealed class Event {
    sealed class TimerAction : Event() {
        data class ToastMessage(@StringRes val message: Int) : TimerAction()
    }

    sealed class Timer : Event() {
        data class Started(val timeRemaining: Long) : Timer()
        object Stopped : Timer()
        data class IsSaved(val saved: Boolean) : Timer()
        data class HasSound(val hasSound: Boolean) : Timer()
    }

    sealed class Error : Event() {
        data class Unknown(val message: String) : Error()
    }
}
