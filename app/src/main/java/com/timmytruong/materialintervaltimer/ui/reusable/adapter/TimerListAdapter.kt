package com.timmytruong.materialintervaltimer.ui.reusable.adapter

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemTimerHorizontalBinding
import com.timmytruong.materialintervaltimer.databinding.ItemTimerVerticalBinding
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.base.screen.Clicks
import com.timmytruong.materialintervaltimer.ui.base.screen.ListItem
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import com.timmytruong.materialintervaltimer.utils.set
import com.timmytruong.materialintervaltimer.utils.toDisplayTime
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

data class TimerItem(
    val id: Int,
    val time: String? = null,
    val title: String? = null,
    val intervalCount: String? = null,
    override val clicks: Clicks
) : ListItem()

@FragmentScoped
class VerticalTimerAdapter @Inject constructor() :
    BaseListAdapter<ItemTimerVerticalBinding, TimerItem>(ItemTimerVerticalBinding::inflate) {

    override fun onBindViewHolder(holder: ViewHolder<ItemTimerVerticalBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val timer = list[position]
        with(holder.view) {
            card.setOnClickListener { timer.click() }
            title.set(timer.title)
            count.set(timer.intervalCount)
            time.set(timer.time)
        }
    }
}

@FragmentScoped
class HorizontalTimerAdapter @Inject constructor() :
    BaseListAdapter<ItemTimerHorizontalBinding, TimerItem>(ItemTimerHorizontalBinding::inflate) {

    override fun onBindViewHolder(holder: ViewHolder<ItemTimerHorizontalBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val timer = list[position]
        with(holder.view) {
            card.setOnClickListener { timer.click() }
            title.set(timer.title)
            count.set(timer.intervalCount)
            time.set(timer.time)
        }
    }
}

fun List<Timer>.toTimerItems(
    resources: ResourceProvider,
    clicks: (Int, Boolean) -> Unit
) = map { timer ->
    TimerItem(
        id = timer.id,
        time = timer.totalTimeMs.toDisplayTime(resources),
        title = timer.title,
        intervalCount = resources.string(R.string.number_of_intervals_format, timer.intervalCount),
        clicks = { clicks.invoke(timer.id, timer.isFavourited) }
    )
}