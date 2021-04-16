package com.timmytruong.materialintervaltimer.utils

import com.timmytruong.materialintervaltimer.utils.constants.SECS_IN_HOUR
import com.timmytruong.materialintervaltimer.utils.constants.SECS_IN_MIN
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

const val DATE_FORMAT = "yyyy-MM-dd"

/**
 * Takes time
 */
fun formatInputtedTime(time: String, format: String): String {
    val filledTime = fillTime(time = time)
    return String.format(
        format,
        "${filledTime[0]}${filledTime[1]}",
        "${filledTime[2]}${filledTime[3]}",
        "${filledTime[4]}${filledTime[5]}"
    )
}

fun formatNormalizedTime(time: String, format: String): String {
    val normalizedTime = normalizeTime(time = time)
    return String.format(
        format,
        "${normalizedTime[0]}${normalizedTime[1]}",
        "${normalizedTime[2]}${normalizedTime[3]}",
        "${normalizedTime[4]}${normalizedTime[5]}"
    )
}

fun formatNormalizedTime(time: Int, format: String): String {
    val newTime = time.toString()
    return formatNormalizedTime(time = newTime, format = format)
}

fun getCurrentDate(): String =
    SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())

fun getSecondsFromTime(time: String): Int {
    val filledTime = fillTime(time)
    return (filledTime.subSequence(0, 2).toString().toInt() * SECS_IN_HOUR) +
            (filledTime.subSequence(2, 4).toString().toInt() * SECS_IN_MIN) +
            filledTime.subSequence(4, 6).toString().toInt()
}

fun getTimeFromSeconds(seconds: Int): String {
    var remainingSeconds = seconds
    val hours = remainingSeconds / SECS_IN_HOUR
    remainingSeconds -= hours * SECS_IN_HOUR
    val mins = remainingSeconds / SECS_IN_MIN
    remainingSeconds -= mins * SECS_IN_MIN

    val secsString = if (remainingSeconds < 10) "0$remainingSeconds" else "$remainingSeconds"
    val minsString = if (mins < 10) "0$mins" else "$mins"
    val hoursString = if (hours < 10) "0$hours" else "$hours"

    return normalizeTime(time = "$hoursString$minsString$secsString")
}

/**
 * Takes a format time and fills the time to ensure size of 6
 * E.g. 4530 -> 004530, 123456 -> 123456, 0 -> 000000
 */
private fun fillTime(time: String): String {
    return if (time.length == 6) time
    else {
        var tempTime = time
        while (tempTime.length < 6) {
            tempTime = "0${tempTime}"
        }
        tempTime
    }
}

/**
 * Normalizes time to correct hour, min, second
 * E.g. 123466 -> 123506
 */
private fun normalizeTime(time: String): String {
    val filledTime = fillTime(time)

    var currentSecs = filledTime.subSequence(4, 6).toString().toInt()
    var currentMins = filledTime.subSequence(2, 4).toString().toInt()
    var currentHours = filledTime.subSequence(0, 2).toString().toInt()

    if (currentSecs >= SECS_IN_MIN) {
        val mins = currentSecs / SECS_IN_MIN
        val remainingSecs = currentSecs % SECS_IN_MIN
        currentMins += mins
        currentSecs = remainingSecs
    }

    if (currentMins >= SECS_IN_MIN) {
        val hours = currentMins / SECS_IN_MIN
        val remainingMins = currentMins % SECS_IN_MIN
        currentHours += hours
        currentMins = remainingMins
    }

    val secsString = if (currentSecs < 10) "0$currentSecs" else "$currentSecs"
    val minsString = if (currentMins < 10) "0$currentMins" else "$currentMins"
    val hoursString = if (currentHours < 10) "0$currentHours" else "$currentHours"

    return "$hoursString$minsString$secsString"
}