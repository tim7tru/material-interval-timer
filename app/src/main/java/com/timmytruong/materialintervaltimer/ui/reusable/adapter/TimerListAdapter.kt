package com.timmytruong.materialintervaltimer.ui.reusable.adapter

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.base.screen.Clicks
import com.timmytruong.materialintervaltimer.base.screen.EmptyClicks
import com.timmytruong.materialintervaltimer.base.screen.ListBinding
import com.timmytruong.materialintervaltimer.databinding.ItemTimerHorizontalBinding
import com.timmytruong.materialintervaltimer.databinding.ItemTimerVerticalBinding
import com.timmytruong.materialintervaltimer.utils.ObservableString
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class VerticalTimerListAdapter @Inject constructor() :
    BaseListAdapter<ItemTimerVerticalBinding, TimerListScreenBinding>() {

    override val bindingLayout: Int
        get() = R.layout.item_timer_vertical

    override fun onBindViewHolder(holder: BaseViewHolder<ItemTimerVerticalBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.view.screen = list[position]
    }
}

@FragmentScoped
class HorizontalTimerItemAdapter @Inject constructor() :
    BaseListAdapter<ItemTimerHorizontalBinding, TimerListScreenBinding>() {

    override val bindingLayout: Int
        get() = R.layout.item_timer_horizontal

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemTimerHorizontalBinding>,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)
        holder.view.screen = list[position]
    }
}

data class TimerListScreenBinding(
    val time: ObservableString = ObservableString(""),
    val title: ObservableString = ObservableString(""),
    val intervalCount: ObservableString = ObservableString(""),
    val timerId: Int,
    override val clicks: Clicks
) : ListBinding()