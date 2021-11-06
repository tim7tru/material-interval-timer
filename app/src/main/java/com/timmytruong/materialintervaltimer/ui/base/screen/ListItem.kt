package com.timmytruong.materialintervaltimer.ui.base.screen

typealias Clicks = (Int) -> Unit

object EmptyClicks: Clicks {
    override fun invoke(p1: Int) {}
}

abstract class ListItem {
    abstract val clicks: Clicks

    var position: Int = -1

    fun click(): Unit = clicks.invoke(position)
}