package com.timmytruong.materialintervaltimer.utils.providers

import android.content.Context
import android.content.res.ColorStateList
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
    fun drawableIdFromTag(tag: String): Int?
    fun tagFromDrawableId(@DrawableRes id: Int): String?
}

class ResourceProviderImpl(override val ctx: Context) : ResourceProvider {

    private val icons = mapOf(
        string(R.string.fitnessTag) to R.drawable.ic_fitness_center,
        string(R.string.personTag) to R.drawable.ic_accessibility_24px,
        string(R.string.speedTag) to R.drawable.ic_speed_24px,
        string(R.string.cafeTag) to R.drawable.ic_local_cafe_24px,
        string(R.string.androidTag) to R.drawable.ic_android_24px,
        string(R.string.musicTag) to R.drawable.ic_audiotrack_24px,
        string(R.string.worldTag) to R.drawable.ic_language_24px,
        string(R.string.emailTag) to R.drawable.ic_email_24px,
        string(R.string.ecoTag) to R.drawable.ic_eco_24px,
        string(R.string.phoneTag) to R.drawable.ic_call_24px,
        string(R.string.playTag) to R.drawable.ic_play,
        string(R.string.pauseTag) to R.drawable.ic_pause_24px
    )

    override fun string(@StringRes id: Int) = ctx.getString(id)

    override fun string(@StringRes id: Int, vararg format: Any) = ctx.getString(id, *format)

    override fun colorList(@ColorRes id: Int) = ContextCompat.getColorStateList(ctx, id)

    override fun color(@ColorRes id: Int) = ContextCompat.getColor(ctx, id)

    override fun drawableIdFromTag(tag: String): Int? = icons[tag]

    override fun tagFromDrawableId(@DrawableRes id: Int): String? = icons.keys.find { id == icons[it] }
}