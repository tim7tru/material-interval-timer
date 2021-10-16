package com.timmytruong.materialintervaltimer.utils

import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_HOUR_L
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_MINS_L
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_SECS_L
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider

typealias ObservableString = ObservableField<String>

fun Fragment.name(): String = this::class.java.simpleName

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