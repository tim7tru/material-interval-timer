package com.timmytruong.materialintervaltimer.ui.reusable

const val PROGRESS_BAR_PROPERTY = "progress"
const val PROGRESS_BAR_ANIMATION_DURATION_MS = 500L
const val PROGRESS_ZERO = 0
const val PROGRESS_HALF = 50
const val PROGRESS_FULL = 100

interface ProgressBar {
    fun toggleProgressBarVisibility(show: Boolean)

    fun updateProgressBar(progress: Int)
}