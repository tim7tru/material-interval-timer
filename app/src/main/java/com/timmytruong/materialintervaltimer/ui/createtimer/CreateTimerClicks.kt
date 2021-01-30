package com.timmytruong.materialintervaltimer.ui.createtimer

import android.view.View

interface CreateTimerClicks {
    interface Main {
        fun goToAddInterval(view: View)

        fun onRepeatClicked(view: View)

        fun onSaveClicked(view: View)

        fun onSoundClicked(view: View)

        fun onStartTimerClicked(view: View)
    }

    interface Interval {
        fun onContinueClicked(view: View)

        fun onIconSwitchClicked(view: View)

        fun onIconClicked(view: View)
    }

    interface Time {
        fun onNumberClicked(view: View)

        fun onBackClicked(view: View)

        fun onAddClicked(view: View)
    }
}
