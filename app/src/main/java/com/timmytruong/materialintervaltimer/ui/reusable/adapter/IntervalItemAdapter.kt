package com.timmytruong.materialintervaltimer.ui.reusable.adapter

import androidx.annotation.DrawableRes
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemIntervalBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.base.adapter.Clicks
import com.timmytruong.materialintervaltimer.ui.base.adapter.EmptyClicks
import com.timmytruong.materialintervaltimer.ui.base.adapter.ListItem
import com.timmytruong.materialintervaltimer.utils.extensions.*
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

data class IntervalItem(
    @DrawableRes val icon: Int? = null,
    val title: String? = null,
    val time: Long? = null,
    val hasHeaders: Boolean = false,
    override val clicks: Clicks = EmptyClicks
): ListItem()

@FragmentScoped
class IntervalItemAdapter @Inject constructor(
    private val resources: ResourceProvider
) : BaseListAdapter<ItemIntervalBinding, IntervalItem>(ItemIntervalBinding::inflate) {

    override fun onBindViewHolder(holder: ViewHolder<ItemIntervalBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val interval = list[position]

        with(holder.view) {
            header.showIf(interval.hasHeaders)

            when (position) {
                0 -> header.text = resources.string(R.string.currentIntervalTitle)
                1 -> header.text = resources.string(R.string.upNextIntervalTitle)
                else -> { /** No op **/ }
            }

            icon.set(interval.icon)
            title.text = interval.title
            time.text = interval.time?.toDisplayTime(resources)
        }
    }
}

fun List<Interval>.toListItems(hasHeaders: Boolean) = this.map {
    IntervalItem(
        icon = it.iconId,
        title = it.name,
        time = it.timeMs,
        hasHeaders = hasHeaders
    )
}