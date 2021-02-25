package com.timmytruong.materialintervaltimer.ui.timer

import android.view.View

interface TimerClicks {
    fun onPlayPauseClicked(view: View)

    fun onStopClicked(view: View)
}