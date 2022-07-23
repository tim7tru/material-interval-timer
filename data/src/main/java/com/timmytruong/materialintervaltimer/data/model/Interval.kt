package com.timmytruong.materialintervaltimer.data.model

data class Interval(
    var name: String = "",
    var iconId: Int? = null,
    var timeMs: Long = 0
) {
    fun clear() {
        name = ""
        iconId = null
        timeMs = 0
    }
}