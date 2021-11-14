package com.timmytruong.materialintervaltimer.utils.extensions

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_HOUR_L
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_MINS_L
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_SECS_L
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider

fun Long.toDisplayTime(res: ResourceProvider): String {
    var time = this

    val hours = time / MILLI_IN_HOUR_L
    time %= MILLI_IN_HOUR_L

    val mins = time / MILLI_IN_MINS_L
    time %= MILLI_IN_MINS_L

    val secs = time / MILLI_IN_SECS_L

    val secsString = if (secs < 10) "0$secs" else "$secs"
    val minsString = if (mins < 10) "0$mins" else "$mins"

    return when {
        hours != 0L -> res.string(R.string.fullTimeFormat, hours, minsString, secsString)
        hours == 0L && mins > 0L -> res.string(R.string.noHourTimeFormat, mins, secsString)
        hours == 0L && mins == 0L -> res.string(R.string.secondsTimeFormat, secs)
        else -> ""
    }
}

fun String.toInputTime(res: ResourceProvider): String {
    val temp = fillTime()
    return res.string(
        R.string.inputTimeFormat,
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
