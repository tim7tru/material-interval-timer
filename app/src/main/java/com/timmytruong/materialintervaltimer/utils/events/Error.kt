package com.timmytruong.materialintervaltimer.utils.events

import com.timmytruong.materialintervaltimer.utils.enums.ErrorType

sealed class Error<T> {
    data class InputError(val error: ErrorType): Error<ErrorType>()
    data class QualifierError(val qualifier: String): Error<String>()
    object UnknownError: Error<Nothing>()
}