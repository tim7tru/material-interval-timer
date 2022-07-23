package com.timmytruong.materialintervaltimer.utils.providers

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.utils.extensions.color

interface PopUpProvider {
    fun showErrorSnackbar(view: View, @StringRes message: Int)

    fun showDialog(
        activity: Activity,
        @StringRes title: Int = 0,
        @StringRes message: Int = 0,
        @StringRes negativeMessage: Int = 0,
        @StringRes positiveMessage: Int = 0,
        clicks: DialogInterface.OnClickListener
    ): AlertDialog

    fun showToast(
        context: Context,
        @StringRes message: Int,
        short: Boolean = true
    )
}

class PopUpProviderImpl: PopUpProvider {
    override fun showErrorSnackbar(view: View, @StringRes message: Int) {
        val context = view.context
        Snackbar.make(view, context.getString(message), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(context.color(R.color.color_red))
            .setTextColor(context.color(R.color.color_white))
            .show()
    }

    override fun showDialog(
        activity: Activity,
        title: Int,
        message: Int,
        negativeMessage: Int,
        positiveMessage: Int,
        clicks: DialogInterface.OnClickListener
    ): AlertDialog = MaterialAlertDialogBuilder(activity)
        .setTitle(activity.getString(title))
        .setMessage(activity.getString(message))
        .setNegativeButton(activity.getString(negativeMessage), clicks)
        .setPositiveButton(activity.getString(positiveMessage), clicks)
        .show()

    override fun showToast(context: Context, message: Int, short: Boolean) {
        Toast.makeText(
            context,
            context.getString(message),
            if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        ).show()
    }
}