package com.timmytruong.materialintervaltimer.ui.base.screen

import android.view.LayoutInflater
import android.view.ViewGroup

abstract class BaseScreen {
    var name: String = ""
}

typealias Inflater<T> = (LayoutInflater, ViewGroup?, Boolean) -> T