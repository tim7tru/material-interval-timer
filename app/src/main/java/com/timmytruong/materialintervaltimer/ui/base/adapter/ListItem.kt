package com.timmytruong.materialintervaltimer.ui.base.adapter

typealias Clicks = (Int) -> Unit

object EmptyClicks: Clicks {
    override fun invoke(position: Int) {}
}

abstract class ListItem {
    abstract val clicks: Clicks

    var position: Int = -1

    fun click(): Unit = clicks.invoke(position)
}