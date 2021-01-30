package com.timmytruong.materialintervaltimer.ui.reusable

const val PROGRESS_BAR_PROPERTY = "progress"
const val PROGRESS_BAR_ANIMATION_DURATION_MS = 500L

interface ProgressBar {
    fun toggleProgressBarVisibility(show: Boolean)

    fun updateProgressBar(progress: Int)
}