package com.timmytruong.materialintervaltimer.model

internal const val TITLE = "title"
internal const val DATE = "date"
internal const val INTERVAL_COUNT = 1
internal const val INTERVAL_ICON_ID = 1
internal const val TOTAL_TIME = 500L
internal const val SOUND_NAME = "sound"
internal const val SOUND_ID = 1
internal const val TIMER_ID = 1

val INTERVAL get() = Interval(
    timeMs = TOTAL_TIME,
    name = TITLE,
    iconId = INTERVAL_ICON_ID
)

internal val INTERVAL_SOUND = IntervalSound(id = SOUND_ID, name = SOUND_NAME)

internal val TIMER get() = Timer(
    title = TITLE,
    createdDate = DATE,
    updatedDate = DATE,
    isFavorited = false,
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
    isFavorited: Boolean = false,
    shouldRepeat: Boolean = false,
    intervals: ArrayList<Interval> = arrayListOf(INTERVAL),
    intervalCount: Int = INTERVAL_COUNT,
    totalTimeMs: Long = TOTAL_TIME,
    intervalSound: IntervalSound = INTERVAL_SOUND
) = Timer(
    title = title,
    createdDate = createdDate,
    updatedDate = updatedDate,
    isFavorited = isFavorited,
    shouldRepeat = shouldRepeat,
    intervals = intervals,
    intervalCount = intervalCount,
    totalTimeMs = totalTimeMs,
    intervalSound = intervalSound
).apply { this.id = id }

fun sounds(): List<IntervalSound> {
    val sounds = mutableListOf<IntervalSound>()
    (1..5).forEach { sounds.add(IntervalSound(id = it, name = "sound$it")) }
    return sounds
}