package com.timmytruong.materialintervaltimer.utils.events

const val INPUT_ERROR = "input error"
const val UNKNOWN_ERROR = "unknown error"
const val EMPTY_ERROR = "empty error"

interface ErrorHandler {
    fun handleInputError()
    fun handleEmptyError()
    fun handleUnknownError()
}