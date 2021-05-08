package com.timmytruong.materialintervaltimer.model

import com.timmytruong.materialintervaltimer.utils.*

private const val EMPTY_INPUT = "000000"
private const val ZERO = "0"
private const val EMPTY_TIME = 0L

private const val MAX_COUNT = 6
private const val MIN_COUNT = 0

data class Time(private var totalMs: Long = EMPTY_TIME) {

    private var inputFormat: String = EMPTY_INPUT
    private var count = MIN_COUNT

    init {
        if (totalMs != EMPTY_TIME) {
            inputFormat = hours(true) + minutes(true) + seconds(true)
            count = MAX_COUNT
            for (ch in inputFormat) {
                if (ch.toString() == ZERO) count--
                else break
            }
        }
    }

    fun clear() {
        totalMs = EMPTY_TIME
        inputFormat = EMPTY_INPUT
    }

    fun getTotalTime() = totalMs

    fun getFormat() = inputFormat

    fun finalize() {
        totalMs = inputFormat.subSequence(0, 2).toString().toLong() * MILLI_PER_HOUR
        totalMs += inputFormat.subSequence(2, 4).toString().toLong() * MILLI_PER_MINUTE
        totalMs += inputFormat.subSequence(4, 6).toString().toLong() * MILLI_PER_SECOND
    }

    fun isInputValid(): Boolean = inputFormat != EMPTY_INPUT

    fun addToInput(number: String) {
        if (count < MAX_COUNT) {
            inputFormat = inputFormat.drop(1).plus(number)
            count++
        }
    }

    fun removeFromInput() {
        if (count > MIN_COUNT) {
            inputFormat = ZERO + inputFormat.dropLast(1)
            count--
        }
    }

    fun hours(fromTotal: Boolean = true) = if (!fromTotal) {
        inputFormat.subSequence(0, 2).toString()
    } else {
        hoursFromMilliseconds(totalMs)
    }

    fun minutes(fromTotal: Boolean = true) = if (!fromTotal) {
        inputFormat.subSequence(2, 4).toString()
    } else {
        minutesFromMilliseconds(totalMs)
    }

    fun seconds(fromTotal: Boolean = true) = if (!fromTotal) {
        inputFormat.subSequence(4, 6).toString()
    } else {
        secondsFromMilliseconds(totalMs)
    }
}