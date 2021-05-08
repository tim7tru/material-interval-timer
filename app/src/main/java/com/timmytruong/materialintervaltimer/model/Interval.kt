package com.timmytruong.materialintervaltimer.model

data class Interval(
    var name: String = "",
    var iconId: Int = 0,
    var time: Time = Time()
) {
    fun clear() {
        name = ""
        iconId = 0
        time = Time()
    }
}