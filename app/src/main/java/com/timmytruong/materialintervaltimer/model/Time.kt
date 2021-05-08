package com.timmytruong.materialintervaltimer.model

import com.timmytruong.materialintervaltimer.utils.*

private const val EMPTY_INPUT = "000000"
private const val ZERO = "0"

data class Time(private var totalMs: Long = 0L) {

    private var inputFormat: String = EMPTY_INPUT

    init {
        if (totalMs != 0L) {
            inputFormat = hours(true) + minutes(true) + seconds(true)
        }
    }

    fun clear() { inputFormat = EMPTY_INPUT }

    fun getTotalTime() = totalMs

    fun finalize() {
        totalMs = inputFormat.subSequence(0, 2).toString().toLong() * MILLI_PER_HOUR
        totalMs += inputFormat.subSequence(2, 4).toString().toLong() * MILLI_PER_MINUTE
        totalMs += inputFormat.subSequence(4, 6).toString().toLong() * MILLI_PER_SECOND
    }

    fun isInputValid(): Boolean = inputFormat != EMPTY_INPUT

    fun addToInput(number: String) {
        if (!(inputFormat == EMPTY_INPUT && number == ZERO)) {
            inputFormat = inputFormat.drop(1).plus(number)
        }
    }

    fun removeFromInput() {
        if (inputFormat != EMPTY_INPUT) {
            inputFormat = ZERO + inputFormat.dropLast(1)
        }
    }

    fun hours(normalized: Boolean = true) = if (!normalized) {
        inputFormat.subSequence(0, 2).toString()
    } else {
        hoursFromMilliseconds(totalMs)
    }

    fun minutes(normalized: Boolean = true) = if (!normalized) {
        inputFormat.subSequence(2, 4).toString()
    } else {
        minutesFromMilliseconds(totalMs)
    }

    fun seconds(normalized: Boolean = true) = if (!normalized) {
        inputFormat.subSequence(4, 6).toString()
    } else {
        secondsFromMilliseconds(totalMs)
    }
}