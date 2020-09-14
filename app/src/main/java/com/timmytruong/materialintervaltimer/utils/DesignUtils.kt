package com.timmytruong.materialintervaltimer.utils

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.utils.constants.AppConstants
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

object DesignUtils {
    fun formatInputtedTime(time: String, format: String): String {
        val filledTime = fillTime(time = time)
        return String.format(format, "${filledTime[0]}${filledTime[1]}", "${filledTime[2]}${filledTime[3]}", "${filledTime[4]}${filledTime[5]}")
    }

    fun formatNormalizedTime(time: String, format: String): String {
        val normalizedTime = normalizeTime(time = fillTime(time = time))
        return String.format(format, "${normalizedTime[0]}${normalizedTime[1]}", "${normalizedTime[2]}${normalizedTime[3]}", "${normalizedTime[4]}${normalizedTime[5]}")
    }

    fun getCurrentDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun getSecondsFromTime(time: String): Int {
        val filledTime =  fillTime(time)
        return (filledTime.subSequence(0, 2).toString().toInt() * 3600) +
                (filledTime.subSequence(2, 4).toString().toInt() * 60) +
                filledTime.subSequence(4, 6).toString().toInt()
    }

    fun getTimeFromSeconds(seconds: Int): String {
        var remainingSeconds = seconds
        val hours = remainingSeconds / 3600
        remainingSeconds -= hours * 3600
        val mins = remainingSeconds / 60
        remainingSeconds -= mins * 60

        val secsString = if (remainingSeconds < 10) "0$remainingSeconds" else "$remainingSeconds"
        val minsString = if (mins < 10) "0$mins" else "$mins"
        val hoursString = if (hours < 10) "0$hours" else "$hours"

        return normalizeTime(time = "$hoursString$minsString$secsString")
    }

    private fun fillTime(time: String): String {
        return if (time.length == 6) time
        else {
            var timeReversed = time.reversed()
            while (timeReversed.length < 6) {
                timeReversed = "${timeReversed}0"
            }
            timeReversed.reversed()
        }
    }

    private fun normalizeTime(time: String): String {
        val filledTime = fillTime(time)

        var currentSecs = filledTime.subSequence(4,6).toString().toInt()
        var currentMins = filledTime.subSequence(2,4).toString().toInt()
        var currentHours = filledTime.subSequence(0,2).toString().toInt()

        if (currentSecs >= 60) {
            val mins = currentSecs / 60
            val remainingSecs = currentSecs % 60
            currentMins += mins
            currentSecs = remainingSecs
        }

        if (currentMins >= 60) {
            val hours = currentMins / 60
            val remainingMins = currentMins % 60
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
            .setBackgroundTint(ContextCompat.getColor(contextView.context, R.color.colorRed))
            .setTextColor(ContextCompat.getColor(contextView.context, R.color.colorWhite))
            .show()
    }

    fun updateProgressBar(view: ProgressBar, progress: Int) {
        val animation = ObjectAnimator.ofInt(view, AppConstants.PROGRESS_BAR_PROPERTY, view.progress, progress)
        animation.duration = AppConstants.PROGRESS_BAR_ANIMATION_DURATION_MS
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }
}