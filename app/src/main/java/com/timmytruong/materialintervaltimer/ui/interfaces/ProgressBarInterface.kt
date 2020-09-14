package com.timmytruong.materialintervaltimer.ui.interfaces

interface ProgressBarInterface {
    fun toggleProgressBarVisibility(show: Boolean)

    fun updateProgressBar(progress: Int)
}