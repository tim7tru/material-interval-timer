package com.timmytruong.materialintervaltimer.model

internal const val TITLE = "title"
internal const val DATE = "date"
internal const val INTERVAL_COUNT = 1
internal const val TOTAL_TIME = 500
internal const val SOUND_NAME = "sound"
internal const val TIMER_ID = 1

val INTERVAL = Interval(timeMs = TOTAL_TIME)

val INTERVAL_SOUND = IntervalSound(name = SOUND_NAME)

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
)