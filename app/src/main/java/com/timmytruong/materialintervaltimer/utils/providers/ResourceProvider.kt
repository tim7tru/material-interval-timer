package com.timmytruong.materialintervaltimer.utils.providers

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.timmytruong.materialintervaltimer.R

interface ResourceProvider {
    val ctx: Context

    fun string(@StringRes id: Int): String
    fun string(@StringRes id: Int, vararg format: Any): String
    fun colorList(@ColorRes id: Int): ColorStateList?
    fun color(@ColorRes id: Int): Int
}

class ResourceProviderImpl(override val ctx: Context) : ResourceProvider {

    override fun string(@StringRes id: Int) = ctx.getString(id)

    override fun string(@StringRes id: Int, vararg format: Any) = ctx.getString(id, *format)

    override fun colorList(@ColorRes id: Int) = ContextCompat.getColorStateList(ctx, id)

    override fun color(@ColorRes id: Int) = ContextCompat.getColor(ctx, id)
}