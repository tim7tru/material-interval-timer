package com.timmytruong.materialintervaltimer.ui.reusable.item

import androidx.annotation.DrawableRes
import com.timmytruong.materialintervaltimer.ui.base.adapter.Clicks
import com.timmytruong.materialintervaltimer.ui.base.adapter.ListItem

data class IconGridItem(
    @DrawableRes val id: Int,
    var isSelected: Boolean,
    override val clicks: Clicks
): ListItem()

fun Int.toIconGridItem(click: Clicks) = IconGridItem(
    id = this,
    isSelected = false,
    clicks = { click(it) }
)