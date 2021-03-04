package com.timmytruong.materialintervaltimer.ui.home

import android.view.View

interface HomeClicks {

    interface BottomSheet {
        fun onStartClicked(view: View)

        fun onFavouriteClicked(view: View)

        fun onDeleteClicked(view: View)
    }
}