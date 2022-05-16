package com.timmytruong.materialintervaltimer.ui.reusable.item

import com.timmytruong.materialintervaltimer.ui.base.adapter.Clicks
import com.timmytruong.materialintervaltimer.ui.base.adapter.ListItem

data class IntervalSoundItem(
    val id: Int,
    val title: String? = null,
    var isSelected: Boolean = false,
    override val clicks: Clicks
): ListItem()