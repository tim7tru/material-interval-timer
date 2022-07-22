package com.timmytruong.materialintervaltimer.ui.reusable.adapter

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemTimerHorizontalBinding
import com.timmytruong.materialintervaltimer.databinding.ItemTimerVerticalBinding
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.item.TimerItem
import com.timmytruong.materialintervaltimer.utils.extensions.toDisplayTime
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class VerticalTimerAdapter @Inject constructor(private val resources: ResourceProvider) :
    BaseListAdapter<ItemTimerVerticalBinding, TimerItem>(ItemTimerVerticalBinding::inflate) {

    private val fallbackTitle = resources.string(R.string.untitled)

    override fun onBindViewHolder(holder: ViewHolder<ItemTimerVerticalBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val timer = list[position]
        with(holder.view) {
            card.setOnClickListener { timer.click() }
            title.text = timer.getDisplayTitle(fallbackTitle)
            timer.intervalCount?.let {
                count.text = resources.string(R.string.number_of_intervals_format, it)
            }
            time.text = timer.time?.toDisplayTime(resources)
        }
    }
}

@FragmentScoped
class HorizontalTimerAdapter @Inject constructor(private val resources: ResourceProvider) :
    BaseListAdapter<ItemTimerHorizontalBinding, TimerItem>(ItemTimerHorizontalBinding::inflate) {

    private val fallbackTitle = resources.string(R.string.untitled)

    override fun onBindViewHolder(holder: ViewHolder<ItemTimerHorizontalBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val timer = list[position]
        with(holder.view) {
            card.setOnClickListener { timer.click() }
            title.text = timer.getDisplayTitle(fallbackTitle)
            timer.intervalCount?.let {
                count.text = resources.string(R.string.number_of_intervals_format, it)
            }
            time.text = timer.time?.toDisplayTime(resources)
        }
    }
}