package com.timmytruong.materialintervaltimer.ui.reusable

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.model.Dialog
import com.timmytruong.materialintervaltimer.databinding.AlertDialogBinding
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class TimerDialog @Inject constructor() {
    private lateinit var binding: AlertDialogBinding

    private lateinit var alert: AlertDialog

    fun build(act: Activity, dialog: Dialog) {
        val inflater = LayoutInflater.from(act)
        binding = DataBindingUtil.inflate(inflater, R.layout.alert_dialog, null, false)
        bindView(dialog)
        val builder = MaterialAlertDialogBuilder(act, R.style.TimerAlertDialog).setView(binding.root)
        alert = builder.create()
    }

    fun show() {
        alert.show()
    }

    private fun bindView(dialog: Dialog) {
        binding.dialog = dialog

        binding.saveAlertPositive.setOnClickListener {
            alert.dismiss()
            dialog.callback?.onPositiveDialogClicked(view = it)
        }

        binding.saveAlertNegative.setOnClickListener {
            alert.dismiss()
            dialog.callback?.onNegativeDialogClicked(view = it)
        }
    }
}

interface DialogClicks {
    fun onPositiveDialogClicked(view: View)

    fun onNegativeDialogClicked(view: View)
}