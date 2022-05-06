package com.timmytruong.materialintervaltimer.utils

import androidx.annotation.StringRes

sealed class Event {

    sealed class BottomSheet : Event() {
        object Dismiss : BottomSheet()

        sealed class TimerAction : BottomSheet() {
            data class ToastMessage(@StringRes val message: Int) : TimerAction()
        }
    }

    data class PlaySound(val id: Int) : Event()

    sealed class Timer : Event() {
        data class Started(val timeRemaining: Long, val progress: Float) : Timer()
        data class IsSaved(val saved: Boolean) : Timer()
        data class HasSound(val hasSound: Boolean) : Timer()
        data class Progress(val progress: Float): Timer()
        object CancelAnimation : Timer()
    }

    sealed class Error : Event() {
        data class Unknown(val message: String) : Error()
    }
}
