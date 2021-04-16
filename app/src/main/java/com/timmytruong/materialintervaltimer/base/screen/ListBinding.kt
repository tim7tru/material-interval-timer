package com.timmytruong.materialintervaltimer.base.screen

typealias Clicks<T> = (T) -> Unit

object EmptyClicks: Clicks<Any> {
    override fun invoke(p1: Any) {}
}

abstract class ListBinding<T> {
    abstract val clicks: Clicks<T>
    open fun click(item: T): Unit = clicks.invoke(item)
}