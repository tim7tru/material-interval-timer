package com.timmytruong.materialintervaltimer.utils.providers

import java.text.SimpleDateFormat
import java.util.*

const val DATE_FORMAT = "yyyy-MM-dd"

interface DateProvider {
    fun getCurrentDate(locale: Locale = Locale.CANADA): String
}

class DateProviderImpl: DateProvider {
    override fun getCurrentDate(locale: Locale): String =
        SimpleDateFormat(DATE_FORMAT, locale).format(Date())
}