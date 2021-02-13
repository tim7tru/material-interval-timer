package com.timmytruong.materialintervaltimer.ui.home.adapters

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.databinding.ItemTimerHorizontalBinding
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.home.HomeViewModel
import com.timmytruong.materialintervaltimer.utils.DesignUtils

class HorizontalTimerItemAdapter(
    private val homeViewModel: HomeViewModel
) : BaseListAdapter<ItemTimerHorizontalBinding, Timer>() {

    override val bindingLayout: Int
        get() = R.layout.item_timer_horizontal

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemTimerHorizontalBinding>,
        position: Int
    ) {
        holder.view.timer = list[position]
        holder.view.homeViewModel = homeViewModel
        val time = DesignUtils.getTimeFromSeconds(list[position].timer_total_time_ms / 1000)
        holder.view.time = DesignUtils.formatNormalizedTime(
            time,
            context.getString(R.string.timerTimeFormat)
        )
    }
}