package com.timmytruong.materialintervaltimer.ui.reusable.item

import com.timmytruong.data.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.adapter.Clicks
import com.timmytruong.materialintervaltimer.ui.base.adapter.ListItem

data class PresetGridItem(
    val timer: Timer,
    override val clicks: Clicks
): ListItem()