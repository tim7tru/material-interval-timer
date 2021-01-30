package com.timmytruong.materialintervaltimer.ui.timer

import android.view.View
import com.timmytruong.materialintervaltimer.ui.reusable.DialogClicks

interface TimerClicks: DialogClicks {
    fun onPlayPauseClicked(view: View)

    fun onStopClicked(view: View)

    fun showCloseDialog(view: View)
}