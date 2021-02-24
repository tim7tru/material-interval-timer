package com.timmytruong.materialintervaltimer.ui.reusable

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.databinding.ItemTimerVerticalBinding
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.list.TimerListViewModel
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class VerticalTimerListAdapter @Inject constructor(
   private val timerListViewModel: TimerListViewModel
): BaseListAdapter<ItemTimerVerticalBinding, Timer>() {

    override val bindingLayout: Int
        get() = R.layout.item_timer_vertical

    override fun onBindViewHolder(holder: BaseViewHolder<ItemTimerVerticalBinding>, position: Int) {
        holder.view.timer = list[position]
        holder.view.timerListViewModel = timerListViewModel
        val time = DesignUtils.getTimeFromSeconds(list[position].timer_total_time_ms / 1000)
        holder.view.time = DesignUtils.formatNormalizedTime(
            time,
            context.getString(R.string.timerTimeFormat)
        )
    }
}