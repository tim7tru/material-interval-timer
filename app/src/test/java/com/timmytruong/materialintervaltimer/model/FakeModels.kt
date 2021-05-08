package com.timmytruong.materialintervaltimer.model

internal const val TITLE = "title"
internal const val DATE = "date"
internal const val INTERVAL_COUNT = 1
internal const val SOUND_NAME = "sound"
internal const val INTERVAL_NAME = "interval"
internal const val TIMER_ID = 1
internal const val ONE_SEC = 1000L
internal const val ONE_SEC_HOUR = "00"
internal const val ONE_SEC_MIN = "00"
internal const val ONE_SEC_SEC = "01"
internal const val ONE_SEC_NORM = "000001"
internal const val FULL_TIME = "111111"
internal const val FULL_TIME_SPLIT = "11"


val TIME = Time(totalMs = ONE_SEC)

val INTERVAL = Interval(
    name = INTERVAL_NAME,
    time = TIME
)

val INTERVAL_SOUND = IntervalSound(name = SOUND_NAME)

val TIMER = Timer(
    title = TITLE,
    createdDate = DATE,
    updatedDate = DATE,
    isFavourited = false,
    shouldRepeat = false,
    intervals = arrayListOf(INTERVAL),
    intervalCount = INTERVAL_COUNT,
    totalTime = TIME,
    intervalSound = INTERVAL_SOUND
).apply { id = TIMER_ID }