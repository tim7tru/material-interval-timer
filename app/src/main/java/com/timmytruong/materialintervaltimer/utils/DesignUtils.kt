package com.timmytruong.materialintervaltimer.utils

import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_FORMAT = "yyyy-MM-dd"

fun currentDate(): String = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())

fun Fragment.name(): String = this::class.java.simpleName