package com.timmytruong.materialintervaltimer.model

data class Interval(
    var name: String = "",
    var iconId: Int = 0,
    var timeMs: Long = 0
) {
    fun clear() {
        name = ""
        iconId = 0
        timeMs = 0
    }
}