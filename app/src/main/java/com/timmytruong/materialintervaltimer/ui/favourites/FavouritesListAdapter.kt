package com.timmytruong.materialintervaltimer.ui.favourites

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.databinding.ItemTimerVerticalBinding
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import javax.inject.Inject

class FavouritesListAdapter @Inject constructor(
   private val favouritesViewModel: FavouritesViewModel
): BaseListAdapter<ItemTimerVerticalBinding, Timer>() {

    override val bindingLayout: Int
        get() = R.layout.item_timer_vertical

    override fun onBindViewHolder(holder: BaseViewHolder<ItemTimerVerticalBinding>, position: Int) {
        holder.view.timer = list[position]
        holder.view.favouritesViewModel = favouritesViewModel
        val time = DesignUtils.getTimeFromSeconds(list[position].timer_total_time_ms / 1000)
        holder.view.time = DesignUtils.formatNormalizedTime(
            time,
            context.getString(R.string.timerTimeFormat)
        )
    }
}