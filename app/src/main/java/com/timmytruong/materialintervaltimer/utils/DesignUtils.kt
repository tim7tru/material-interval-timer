package com.timmytruong.materialintervaltimer.utils

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.utils.constants.DATE_FORMAT
import com.timmytruong.materialintervaltimer.utils.constants.SECS_IN_HOUR
import com.timmytruong.materialintervaltimer.utils.constants.SECS_IN_MIN
import java.text.SimpleDateFormat
import java.util.*

object DesignUtils {
    /**
     * Takes time
     */
    fun formatInputtedTime(time: String, format: String): String {
        val filledTime = fillTime(time = time)
        return String.format(
            format,
            "${filledTime[0]}${filledTime[1]}",
            "${filledTime[2]}${filledTime[3]}",
            "${filledTime[4]}${filledTime[5]}"
        )
    }

    fun formatNormalizedTime(time: String, format: String): String {
        val normalizedTime = normalizeTime(time = time)
        return String.format(
            format,
            "${normalizedTime[0]}${normalizedTime[1]}",
            "${normalizedTime[2]}${normalizedTime[3]}",
            "${normalizedTime[4]}${normalizedTime[5]}"
        )
    }

    fun formatNormalizedTime(time: Int, format: String): String {
        val newTime = time.toString()
        return formatNormalizedTime(time = newTime, format = format)
    }

    fun getCurrentDate(): String =
        SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())

    fun getSecondsFromTime(time: String): Int {
        val filledTime = fillTime(time)
        return (filledTime.subSequence(0, 2).toString().toInt() * SECS_IN_HOUR) +
                (filledTime.subSequence(2, 4).toString().toInt() * SECS_IN_MIN) +
                filledTime.subSequence(4, 6).toString().toInt()
    }

    fun getTimeFromSeconds(seconds: Int): String {
        var remainingSeconds = seconds
        val hours = remainingSeconds / SECS_IN_HOUR
        remainingSeconds -= hours * SECS_IN_HOUR
        val mins = remainingSeconds / SECS_IN_MIN
        remainingSeconds -= mins * SECS_IN_MIN

        val secsString = if (remainingSeconds < 10) "0$remainingSeconds" else "$remainingSeconds"
        val minsString = if (mins < 10) "0$mins" else "$mins"
        val hoursString = if (hours < 10) "0$hours" else "$hours"

        return normalizeTime(time = "$hoursString$minsString$secsString")
    }

    /**
     * Takes a format time and fills the time to ensure size of 6
     * E.g. 4530 -> 004530, 123456 -> 123456, 0 -> 000000
     */
    private fun fillTime(time: String): String {
        return if (time.length == 6) time
        else {
            var tempTime = time
            while (tempTime.length < 6) {
                tempTime = "0${tempTime}"
            }
            tempTime
        }
    }

    /**
     * Normalizes time to correct hour, min, second
     * E.g. 123466 -> 123506
     */
    private fun normalizeTime(time: String): String {
        val filledTime = fillTime(time)

        var currentSecs = filledTime.subSequence(4, 6).toString().toInt()
        var currentMins = filledTime.subSequence(2, 4).toString().toInt()
        var currentHours = filledTime.subSequence(0, 2).toString().toInt()

        if (currentSecs >= SECS_IN_MIN) {
            val mins = currentSecs / SECS_IN_MIN
            val remainingSecs = currentSecs % SECS_IN_MIN
            currentMins += mins
            currentSecs = remainingSecs
        }

        if (currentMins >= SECS_IN_MIN) {
            val hours = currentMins / SECS_IN_MIN
            val remainingMins = currentMins % SECS_IN_MIN
            currentHours += hours
            currentMins = remainingMins
        }

        val secsString = if (currentSecs < 10) "0$currentSecs" else "$currentSecs"
        val minsString = if (currentMins < 10) "0$currentMins" else "$currentMins"
        val hoursString = if (currentHours < 10) "0$currentHours" else "$currentHours"

        return "$hoursString$minsString$secsString"
    }

    fun showSnackbarError(contextView: View, message: String) {
        Snackbar.make(contextView, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(getColor(contextView.context, R.color.colorRed))
            .setTextColor(getColor(contextView.context, R.color.colorWhite))
            .show()
    }

    fun getTagFromDrawableId(context: Context, id: Int): String? {
        return when (id) {
            R.drawable.ic_fitness_center -> context.getString(R.string.fitnessTag)
            R.drawable.ic_accessibility_24px -> context.getString(R.string.personTag)
            R.drawable.ic_speed_24px -> context.getString(R.string.speedTag)
            R.drawable.ic_local_cafe_24px -> context.getString(R.string.cafeTag)
            R.drawable.ic_android_24px -> context.getString(R.string.androidTag)
            R.drawable.ic_audiotrack_24px -> context.getString(R.string.musicTag)
            R.drawable.ic_language_24px -> context.getString(R.string.worldTag)
            R.drawable.ic_email_24px -> context.getString(R.string.emailTag)
            R.drawable.ic_eco_24px -> context.getString(R.string.ecoTag)
            R.drawable.ic_call_24px -> context.getString(R.string.phoneTag)
            R.drawable.ic_play -> context.getString(R.string.playTag)
            R.drawable.ic_pause_24px -> context.getString(R.string.pauseTag)
            else -> null
        }
    }

    fun getDrawableIdFromTag(context: Context, tag: String): Int {
        return when(tag) {
            context.getString(R.string.fitnessTag) -> R.drawable.ic_fitness_center
            context.getString(R.string.personTag) -> R.drawable.ic_accessibility_24px
            context.getString(R.string.speedTag) -> R.drawable.ic_speed_24px
            context.getString(R.string.cafeTag) -> R.drawable.ic_local_cafe_24px
            context.getString(R.string.androidTag) -> R.drawable.ic_android_24px
            context.getString(R.string.musicTag) -> R.drawable.ic_audiotrack_24px
            context.getString(R.string.worldTag) -> R.drawable.ic_language_24px
            context.getString(R.string.emailTag) -> R.drawable.ic_email_24px
            context.getString(R.string.ecoTag) -> R.drawable.ic_eco_24px
            context.getString(R.string.phoneTag) -> R.drawable.ic_call_24px
            context.getString(R.string.playTag) -> R.drawable.ic_play
            context.getString(R.string.pauseTag) -> R.drawable.ic_pause_24px
            else -> 0
        }
    }

    fun getColorList(context: Context, colorRes: Int) =
        ContextCompat.getColorStateList(context, colorRes)

    fun getColor(context: Context, colorRes: Int) = ContextCompat.getColor(context, colorRes)

    fun showDialog(
        context: Context,
        title: String = "",
        message: String = "",
        neutralMessage: String = "",
        negativeMessage: String = "",
        positiveMessage: String = "",
        clicks: DialogInterface.OnClickListener
    ): AlertDialog = MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(message)
        .setNeutralButton(neutralMessage, clicks)
        .setNegativeButton(negativeMessage, clicks)
        .setPositiveButton(positiveMessage, clicks)
        .show()
}