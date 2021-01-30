package com.timmytruong.materialintervaltimer.ui.home

import android.view.View

interface Home {
    interface Main {
        fun onAddClicked(view: View)
    }

    interface BottomSheet {
        fun onStartClicked(view: View)

        fun onFavouriteClicked(view: View)

        fun onDeleteClicked(view: View)
    }
}