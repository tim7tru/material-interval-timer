package com.timmytruong.materialintervaltimer.model

data class Interval(
    var interval_name: String = "",
    var interval_icon_id: Int = 0,
    var interval_time_ms: Int = 0,
    var interval_time_format: String = ""
) {
    fun clear() {
        interval_name = ""
        interval_icon_id = 0
        interval_time_ms = 0
        interval_time_format = ""
    }
}