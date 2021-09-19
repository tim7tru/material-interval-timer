package com.timmytruong.materialintervaltimer.utils.providers

import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.utils.ResourceProvider

interface PopUpProvider {
    fun showErrorSnackbar(view: View, @StringRes message: Int)

    fun showDialog(
        @StringRes title: Int,
        @StringRes message: Int,
        @StringRes neutralMessage: Int,
        @StringRes negativeMessage: Int,
        @StringRes positiveMessage: Int,
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
        title: Int,
        message: Int,
        neutralMessage: Int,
        negativeMessage: Int,
        positiveMessage: Int,
        clicks: DialogInterface.OnClickListener
    ): AlertDialog = MaterialAlertDialogBuilder(resources.ctx)
        .setTitle(resources.string(title))
        .setMessage(resources.string(message))
        .setNeutralButton(resources.string(neutralMessage), clicks)
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