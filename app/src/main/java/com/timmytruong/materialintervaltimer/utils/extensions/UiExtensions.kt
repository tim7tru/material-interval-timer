package com.timmytruong.materialintervaltimer.utils.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.google.android.material.textfield.TextInputEditText

const val INVISIBLE = View.INVISIBLE
const val SHOW = View.VISIBLE
const val HIDE = View.GONE

typealias Inflater<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

fun MenuItem?.show() { this?.let { isVisible = true } }

fun MenuItem?.hide() { this?.let { isVisible = false } }

fun MenuItem?.showIf(condition: Boolean) {
    this?.let { if (condition) show() else hide() }
}

fun View.show() { this.visibility = SHOW }

fun View.hide() { this.visibility = HIDE }

fun View.invisible() { this.visibility = INVISIBLE }

fun View.hideKeyboard() {
    val imm = context.getSystemService(InputMethodManager::class.java)
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showIf(condition: Boolean) { visibility = if (condition) SHOW else HIDE }

fun ViewGroup.showIf(condition: Boolean) { visibility = if (condition) SHOW else HIDE}

fun ImageView.set(@DrawableRes id: Int?) = with(this) {
    if (id != null) {
        show()
        setImageResource(id)
    } else {
        hide()
    }
}

fun TextInputEditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(text: Editable?) { onTextChanged(text.toString()) }
        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })
}
