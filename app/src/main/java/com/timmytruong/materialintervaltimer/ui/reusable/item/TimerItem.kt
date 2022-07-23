package com.timmytruong.materialintervaltimer.ui.reusable.item

import com.timmytruong.data.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.adapter.Clicks
import com.timmytruong.materialintervaltimer.ui.base.adapter.ListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class TimerItem(
    val id: Int,
    val time: Long? = null,
    val title: String? = null,
    val intervalCount: Int? = null,
    override val clicks: Clicks
) : ListItem() {

    fun getDisplayTitle(fallback: String) = if (title.isNullOrEmpty()) fallback else title
}

fun Flow<List<Timer>>.toTimerItems(number: Int = 0, onTimerClicked: (Int, Boolean) -> Unit) = map { list ->
    if (number == 0) list.map { it.toTimerItem(onTimerClicked) }
    else list.trim(number).map { it.toTimerItem(onTimerClicked) }
}

private fun List<Timer>.trim(number: Int) = when {
    size < number -> subList(0, size)
    else -> subList(0, number)
}

private fun Timer.toTimerItem(clicks: (Int, Boolean) -> Unit) = TimerItem(
    id = id,
    time = totalTimeMs,
    title = title,
    intervalCount = intervalCount,
    clicks = { clicks.invoke(id, isFavorited) }
)