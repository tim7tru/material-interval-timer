package com.timmytruong.materialintervaltimer.utils.extensions

import com.timmytruong.materialintervaltimer.utils.constants.*
import kotlin.math.ceil

fun Long.toDisplayTime(isCountdown: Boolean = false): Triple<String?, String?, String> {
    var time = this

    val hours = (time / MILLI_IN_HOUR_L).toInt()
    time %= MILLI_IN_HOUR_L

    val mins = (time / MILLI_IN_MINS_L).toInt()
    time %= MILLI_IN_MINS_L

    val secs = ceil((time / MILLI_IN_SECS_L).toDouble()).toInt()

    val secsString = if (secs < 10 && !isCountdown) "0$secs" else "$secs"
    val minsString = if (mins < 10 && !isCountdown) "0$mins" else "$mins"

    return when {
        hours != 0 -> Triple("$hours", minsString, secsString)
        else-> Triple(null, minsString, secsString)
    }
}

fun String.toInputTime(format: String): String {
    val temp = fillTime()
    return String.format(
        format,
        temp.subSequence(0, 2),
        temp.subSequence(2, 4),
        temp.subSequence(4, 6)
    )
}

fun String.getTimeMs(): Long {
    val temp = fillTime()
    var milliseconds = 0L
    milliseconds += temp.subSequence(4, 6).toString().toLong() * MILLI_IN_SECS_L
    milliseconds += temp.subSequence(2, 4).toString().toLong() * MILLI_IN_MINS_L
    milliseconds += temp.subSequence(0, 2).toString().toLong() * MILLI_IN_HOUR_L
    return milliseconds
}

private fun String.fillTime(): String {
    var temp = this
    return when (temp.length >= 6) {
        true -> temp
        false -> {
            while (temp.length < 6) {
                temp = "0${temp}"
            }
            temp
        }
    }
}
