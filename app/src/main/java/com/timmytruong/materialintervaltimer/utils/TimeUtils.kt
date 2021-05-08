package com.timmytruong.materialintervaltimer.utils

const val MILLI_PER_SECOND = 1000
const val MILLI_PER_MINUTE = 60 * MILLI_PER_SECOND
const val MILLI_PER_HOUR = 60 * MILLI_PER_MINUTE

fun hoursFromMilliseconds(milliseconds: Long): String {
    val hours = milliseconds / MILLI_PER_HOUR
    return if (hours < 10) "0$hours" else "$hours"
}

fun minutesFromMilliseconds(milliseconds: Long): String {
    val minutes = (milliseconds % MILLI_PER_HOUR) / MILLI_PER_MINUTE
    return if (minutes < 10) "0$minutes" else "$minutes"
}

fun secondsFromMilliseconds(milliseconds: Long): String {
    val seconds = ((milliseconds % MILLI_PER_HOUR) % MILLI_PER_MINUTE) / MILLI_PER_SECOND
    return if (seconds < 10) "0$seconds" else "$seconds"
}