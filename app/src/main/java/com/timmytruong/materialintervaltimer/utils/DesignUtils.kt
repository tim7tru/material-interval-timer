package com.timmytruong.materialintervaltimer.utils

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.timmytruong.materialintervaltimer.R

fun showSnackbarError(contextView: View, message: String) {
    Snackbar.make(contextView, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(contextView.color(R.color.colorRed))
        .setTextColor(contextView.color(R.color.colorWhite))
        .show()
}

fun getTagFromDrawableId(context: Context, @DrawableRes id: Int): String? {
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
    return when (tag) {
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

fun showToast(context: Context, message: String, short: Boolean = true) {
    Toast.makeText(
        context,
        message,
        if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()
}

fun Context.colorList(@ColorRes colorRes: Int) = ContextCompat.getColorStateList(this, colorRes)
fun View.colorList(@ColorRes colorRes: Int) = this.context.colorList(colorRes)

fun Context.color(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)
fun View.color(@ColorRes colorRes: Int) = this.context.color(colorRes)

fun Context.string(@StringRes id: Int) = this.getString(id)
fun Fragment.string(@StringRes id: Int) = getString(id)

fun Fragment.name(): String = this::class.java.simpleName

fun formatString(a: String, vararg fillers: String) = String.format(a, *fillers)
