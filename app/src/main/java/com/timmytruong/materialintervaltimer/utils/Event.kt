package com.timmytruong.materialintervaltimer.utils

import androidx.annotation.StringRes

sealed class Event {

    data class PlaySound(val id: Int) : Event()

    sealed class BottomSheet : Event() {
        object Dismiss : BottomSheet()

        sealed class TimerAction : BottomSheet() {
            data class ToastMessage(@StringRes val message: Int) : TimerAction()
        }
    }

    sealed class Timer : Event() {
        data class Started(val timeRemaining: Long) : Timer()
        data class IsSaved(val saved: Boolean) : Timer()
        data class HasSound(val hasSound: Boolean) : Timer()
        object Stopped : Timer()
        object Bindings : Timer()
    }

    sealed class Error : Event() {
        data class Unknown(val message: String) : Error()
    }
}
