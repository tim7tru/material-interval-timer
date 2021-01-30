package com.timmytruong.materialintervaltimer.ui.interfaces

import android.view.View

interface OnClickListeners {

    interface TimerFrag : DialogCallback {
        fun onPlayPauseClicked(view: View)

        fun onStopClicked(view: View)

        fun showCloseDialog(view: View)
    }

    interface DialogCallback {
        fun onPositiveDialogClicked(view: View)

        fun onNegativeDialogClicked(view: View)
    }
}