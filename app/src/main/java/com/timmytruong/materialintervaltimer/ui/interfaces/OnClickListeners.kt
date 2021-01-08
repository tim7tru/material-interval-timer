package com.timmytruong.materialintervaltimer.ui.interfaces

import android.view.View

interface OnClickListeners {
    interface CreateIntervalTimeFrag {
        fun onNumberClicked(view: View)

        fun onBackClicked(view: View)

        fun onAddClicked(view: View)
    }

    interface CreateIntervalFrag {
        fun onContinueClicked(view: View)

        fun onIconSwitchClicked(view: View)

        fun onIconClicked(view: View)
    }

    interface CreateTimerFrag {
        fun goToAddInterval(view: View)

        fun onRepeatClicked(view: View)

        fun onSaveClicked(view: View)

        fun onSoundClicked(view: View)

        fun onStartTimerClicked(view: View)

        fun goToHome(view: View)
    }

    interface HomeFrag {
        fun onTimerClicked(view: View)
    }

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