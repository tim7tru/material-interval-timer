package com.timmytruong.materialintervaltimer.utils.providers

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.timmytruong.materialintervaltimer.R

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
        @StringRes message: Int,
        short: Boolean = true
    )
}

class PopUpProviderImpl(private val resources: ResourceProvider): PopUpProvider {
    override fun showErrorSnackbar(view: View, @StringRes message: Int) {
        Snackbar.make(view, resources.string(message), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.color(R.color.colorRed))
            .setTextColor(resources.color(R.color.colorWhite))
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
        .setTitle(resources.string(title))
        .setMessage(resources.string(message))
        .setNegativeButton(resources.string(negativeMessage), clicks)
        .setPositiveButton(resources.string(positiveMessage), clicks)
        .show()

    override fun showToast(message: Int, short: Boolean) {
        Toast.makeText(
            resources.ctx,
            resources.string(message),
            if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        ).show()
    }
}