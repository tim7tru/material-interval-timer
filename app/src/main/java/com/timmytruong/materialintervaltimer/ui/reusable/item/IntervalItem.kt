package com.timmytruong.materialintervaltimer.ui.reusable.item

import androidx.annotation.DrawableRes
import com.timmytruong.materialintervaltimer.data.model.Interval
import com.timmytruong.materialintervaltimer.ui.base.adapter.Clicks
import com.timmytruong.materialintervaltimer.ui.base.adapter.EmptyClicks
import com.timmytruong.materialintervaltimer.ui.base.adapter.ListItem

data class IntervalItem(
    @DrawableRes val icon: Int? = null,
    val title: String,
    val time: Long? = null,
    val hasHeaders: Boolean = false,
    override val clicks: Clicks = EmptyClicks
): ListItem()

fun Interval.toListItem(hasHeaders: Boolean) = IntervalItem(
    icon = iconId,
    title = name,
    time = timeMs,
    hasHeaders = hasHeaders
)