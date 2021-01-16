package com.timmytruong.materialintervaltimer.utils

import com.timmytruong.materialintervaltimer.utils.enums.ErrorType

sealed class Error<T> {
    data class InputError(val error: ErrorType): Error<ErrorType>()
    object UnknownError: Error<Nothing>()
}