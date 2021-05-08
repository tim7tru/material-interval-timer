package com.timmytruong.materialintervaltimer.ui.reusable

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.scopes.ViewScoped
import javax.inject.Inject
import javax.inject.Singleton

@FragmentScoped
class MITSnackbar @Inject constructor(private val resources: ResourceProvider) {

    fun showError(view: View, @StringRes messageId: Int) {
        create(view, resources.string(messageId))
            .setBackgroundTint(resources.color(R.color.colorRed))
            .setTextColor(resources.color(R.color.colorWhite))
            .show()
    }

    private fun create(view: View, message: String, length: Int = Snackbar.LENGTH_SHORT): Snackbar {
        return Snackbar.make(view, message, length)
    }
}

@FragmentScoped
class MITDialog @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resources: ResourceProvider
) {

    fun showDialog(
        @StringRes title: Int = 0,
        @StringRes message: Int = 0,
        @StringRes neutralMessage: Int = 0,
        @StringRes negativeMessage: Int = 0,
        @StringRes positiveMessage: Int = 0,
        clicks: DialogInterface.OnClickListener
    ): AlertDialog = builder()
        .setTitle(resources.string(title))
        .setMessage(resources.string(message))
        .setNeutralButton(resources.string(neutralMessage), clicks)
        .setNegativeButton(resources.string(negativeMessage), clicks)
        .setPositiveButton(resources.string(positiveMessage), clicks)
        .show()

    private fun builder() = MaterialAlertDialogBuilder(context)
}

@FragmentScoped
class MITToast @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resources: ResourceProvider
) {

    fun showToast(@StringRes message: Int, short: Boolean) {
        Toast.makeText(
            context,
            resources.string(message),
            if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        ).show()
    }
}