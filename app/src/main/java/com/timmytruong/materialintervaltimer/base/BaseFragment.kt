package com.timmytruong.materialintervaltimer.base

import androidx.lifecycle.Observer
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType

interface BaseFragment<T> {
    val errorObserver: Observer<ErrorType>

    fun subscribeObservers(viewModel: T)

    fun bindOnClick()
}