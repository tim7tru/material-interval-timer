package com.timmytruong.materialintervaltimer.utils.providers

import android.content.Context
import android.content.res.ColorStateList
import android.media.MediaPlayer
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.timmytruong.materialintervaltimer.R

interface ResourceProvider {

    val icons: Map<String, Int>

    @ColorInt
    fun color(@ColorRes id: Int): Int
    fun colorStateList(@ColorRes id: Int): ColorStateList?
    fun string(@StringRes id: Int): String
    fun string(@StringRes id: Int, vararg args: Any): String
    fun tagFromDrawableId(@DrawableRes id: Int): String?
    fun drawableIdFromTag(tag: String): Int?
    fun playSound(id: Int)
    fun context(): Context
}

class AppResourceProvider(private val context: Context) : ResourceProvider {

    override val icons: Map<String, Int> = mapOf(
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

    override fun color(@ColorRes id: Int): Int = ContextCompat.getColor(context, id)

    override fun colorStateList(@ColorRes id: Int): ColorStateList? =
        ContextCompat.getColorStateList(context, id)

    override fun string(@StringRes id: Int): String = context.getString(id)

    override fun string(@StringRes id: Int, vararg args: Any): String {
        return context.getString(id, *args)
    }

    override fun tagFromDrawableId(id: Int): String? {
        for (key in icons.keys) {
            if (id == icons[key]) {
                return key
            }
        }
        return null
    }

    override fun drawableIdFromTag(tag: String): Int? = icons[tag]

    override fun playSound(id: Int) {
        if (id != -1) MediaPlayer.create(context, id).start()
    }

    override fun context(): Context = context
}