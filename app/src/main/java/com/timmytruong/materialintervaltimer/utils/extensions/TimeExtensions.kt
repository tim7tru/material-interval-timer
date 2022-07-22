package com.timmytruong.materialintervaltimer.utils.extensions

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.utils.constants.*
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import kotlin.math.ceil

fun Long.toDisplayTime(res: ResourceProvider): String {
    var time = this

    val hours = (time.toDouble() / MILLI_IN_HOUR_D).toInt()
    time %= MILLI_IN_HOUR_L

    val mins = (time.toDouble() / MILLI_IN_MINS_D).toInt()
    time %= MILLI_IN_MINS_L

    val secs = ceil(time.toDouble() / MILLI_IN_SECS_D).toInt()

    val secsString = if (secs < 10) "0$secs" else "$secs"
    val minsString = if (mins < 10) "0$mins" else "$mins"

    return when {
        hours != 0 -> res.string(R.string.full_time_format, hours, minsString, secsString)
        hours == 0 && mins > 0 -> res.string(R.string.no_hour_time_format, mins, secsString)
        hours == 0 && mins == 0 -> res.string(R.string.seconds_time_format, secs)
        else -> ""
    }
}

fun String.toInputTime(res: ResourceProvider): String {
    val temp = fillTime()
    return res.string(
        R.string.input_time_format,
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
