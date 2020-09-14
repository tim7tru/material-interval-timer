package com.timmytruong.materialintervaltimer.model

object MockData {
    val timer = Timer(
        timer_title = "Title",
        timer_saved = false,
        timer_intervals_count = "999",
        timer_total_time_secs = "999"
    )

    val timerList = arrayListOf(timer, timer, timer, timer)

    val interval = Interval(
        interval_name = "interval int"
    )
}