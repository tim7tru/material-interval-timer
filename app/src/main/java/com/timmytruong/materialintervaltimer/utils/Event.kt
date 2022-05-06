package com.timmytruong.materialintervaltimer.utils

import androidx.annotation.StringRes
import androidx.navigation.NavDirections

sealed class Event {

    data class Navigate(val directions: NavDirections): Event()
    data class ToastMessage(@StringRes val message: Int): Event()

    sealed class BottomSheet : Event() {
        object Dismiss : BottomSheet()
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
