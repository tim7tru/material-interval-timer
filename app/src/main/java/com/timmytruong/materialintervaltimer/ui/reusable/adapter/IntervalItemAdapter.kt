package com.timmytruong.materialintervaltimer.ui.reusable.adapter

import androidx.annotation.DrawableRes
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemIntervalBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.ui.base.BaseListAdapter2
import com.timmytruong.materialintervaltimer.ui.base.screen.Clicks
import com.timmytruong.materialintervaltimer.ui.base.screen.EmptyClicks
import com.timmytruong.materialintervaltimer.ui.base.screen.ListItem
import com.timmytruong.materialintervaltimer.utils.hide
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import com.timmytruong.materialintervaltimer.utils.set
import com.timmytruong.materialintervaltimer.utils.show
import com.timmytruong.materialintervaltimer.utils.toDisplayTime
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

data class IntervalItem(
    @DrawableRes val icon: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val hasHeaders: Boolean = false,
    override val clicks: Clicks = EmptyClicks
): ListItem()

@FragmentScoped
class IntervalItemAdapter @Inject constructor():
    BaseListAdapter2<ItemIntervalBinding, IntervalItem>(ItemIntervalBinding::inflate) {

    override fun onBindViewHolder(holder: ViewHolder<ItemIntervalBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val interval = list[position]

        with(holder.view) {
            if (interval.hasHeaders) {
                header.show()
                when (position) {
                    0 -> header.set(R.string.currentIntervalTitle)
                     1 -> header.set(R.string.upNextIntervalTitle)
                    else -> { /** No op **/ }
                }
            } else {
                header.hide()
            }

            icon.set(interval.icon)
            title.set(interval.title)
            time.set(interval.description)
        }
    }
}

fun List<Interval>.toListItems(resources: ResourceProvider, hasHeaders: Boolean) = this.map {
    IntervalItem(
        icon = it.iconId,
        title = it.name,
        description = it.timeMs.toDisplayTime(resources),
        hasHeaders = hasHeaders
    )
}