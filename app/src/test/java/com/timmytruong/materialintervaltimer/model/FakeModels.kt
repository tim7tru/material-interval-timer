package com.timmytruong.materialintervaltimer.model

internal const val TITLE = "title"
internal const val DATE = "date"
internal const val INTERVAL_COUNT = 1
internal const val TOTAL_TIME = 500L
internal const val SOUND_NAME = "sound"
internal const val SOUND_ID = 1
internal const val TIMER_ID = 1

val INTERVAL = Interval(timeMs = TOTAL_TIME)

val INTERVAL_SOUND = IntervalSound(id = SOUND_ID, name = SOUND_NAME)

val TIMER = Timer(
    title = TITLE,
    createdDate = DATE,
    updatedDate = DATE,
    isFavourited = false,
    shouldRepeat = false,
    intervals = arrayListOf(INTERVAL),
    intervalCount = INTERVAL_COUNT,
    totalTimeMs = TOTAL_TIME,
    intervalSound = INTERVAL_SOUND
).apply { id = TIMER_ID }

fun timer(
    id: Int = TIMER_ID,
    title: String = TITLE,
    createdDate: String = DATE,
    updatedDate: String = DATE,
    isFavourited: Boolean = false,
    shouldRepeat: Boolean = false,
    intervals: ArrayList<Interval> = arrayListOf(INTERVAL),
    intervalCount: Int = INTERVAL_COUNT,
    totalTimeMs: Long = TOTAL_TIME,
    intervalSound: IntervalSound = INTERVAL_SOUND
) = Timer(
    title = title,
    createdDate = createdDate,
    updatedDate = updatedDate,
    isFavourited = isFavourited,
    shouldRepeat = shouldRepeat,
    intervals = intervals,
    intervalCount = intervalCount,
    totalTimeMs = totalTimeMs,
    intervalSound = intervalSound
).apply { this.id = id }