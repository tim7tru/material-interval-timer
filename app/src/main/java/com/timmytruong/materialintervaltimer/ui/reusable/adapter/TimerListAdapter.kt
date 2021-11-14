package com.timmytruong.materialintervaltimer.ui.reusable.adapter

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemTimerHorizontalBinding
import com.timmytruong.materialintervaltimer.databinding.ItemTimerVerticalBinding
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.base.adapter.Clicks
import com.timmytruong.materialintervaltimer.ui.base.adapter.ListItem
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import com.timmytruong.materialintervaltimer.utils.extensions.toDisplayTime
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

data class TimerItem(
    val id: Int,
    val time: Long? = null,
    val title: String? = null,
    val intervalCount: Int? = null,
    override val clicks: Clicks
) : ListItem()

@FragmentScoped
class VerticalTimerAdapter @Inject constructor(private val resources: ResourceProvider) :
    BaseListAdapter<ItemTimerVerticalBinding, TimerItem>(ItemTimerVerticalBinding::inflate) {

    override fun onBindViewHolder(holder: ViewHolder<ItemTimerVerticalBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val timer = list[position]
        with(holder.view) {
            card.setOnClickListener { timer.click() }
            title.text = timer.title
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

    override fun onBindViewHolder(holder: ViewHolder<ItemTimerHorizontalBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val timer = list[position]
        with(holder.view) {
            card.setOnClickListener { timer.click() }
            title.text = timer.title
            timer.intervalCount?.let {
                count.text = resources.string(R.string.number_of_intervals_format, it)
            }
            time.text = timer.time?.toDisplayTime(resources)
        }
    }
}

fun List<Timer>.toTimerItems(clicks: (Int, Boolean) -> Unit) = map { timer ->
    TimerItem(
        id = timer.id,
        time = timer.totalTimeMs,
        title = timer.title,
        intervalCount = timer.intervalCount,
        clicks = { clicks.invoke(timer.id, timer.isFavorited) }
    )
}