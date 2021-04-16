package com.timmytruong.materialintervaltimer.ui.reusable

import androidx.databinding.ObservableField
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.base.screen.Clicks
import com.timmytruong.materialintervaltimer.base.screen.ListBinding
import com.timmytruong.materialintervaltimer.databinding.ItemTimerHorizontalBinding
import com.timmytruong.materialintervaltimer.databinding.ItemTimerVerticalBinding
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class VerticalTimerListAdapter @Inject constructor() :
    BaseListAdapter<ItemTimerVerticalBinding, TimerListScreenBinding>() {

    override val bindingLayout: Int
        get() = R.layout.item_timer_vertical

    override fun onBindViewHolder(holder: BaseViewHolder<ItemTimerVerticalBinding>, position: Int) {
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
        holder.view.screen = list[position]
    }
}

data class TimerListScreenBinding(
    val time: ObservableField<String> = ObservableField(""),
    val title: ObservableField<String> = ObservableField(""),
    val intervalCount: ObservableField<String> = ObservableField(""),
    val timerId: Int,
    override val clicks: Clicks<TimerListScreenBinding>
) : ListBinding<TimerListScreenBinding>()