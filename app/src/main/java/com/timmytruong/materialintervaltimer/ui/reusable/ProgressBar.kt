package com.timmytruong.materialintervaltimer.ui.reusable

import android.animation.ObjectAnimator
import android.view.animation.BaseInterpolator
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.BaseProgressIndicatorSpec
import dagger.hilt.android.scopes.ViewScoped
import javax.inject.Inject
import javax.inject.Qualifier

private const val PROGRESS_BAR_PROPERTY = "progress"
private const val PROGRESS_BAR_ANIMATION_DURATION_MS = 500L

interface ProgressBar {
    fun updateProgressBar(progress: Int, show: Boolean = true)
}

class ProgressAnimation(private val progressInterpolator: BaseInterpolator) {

    private var animation: ObjectAnimator? = null

    fun <T : BaseProgressIndicatorSpec> startAnimation(
        target: BaseProgressIndicator<T>?,
        start: Int,
        end: Int,
        duration: Long = PROGRESS_BAR_ANIMATION_DURATION_MS
    ) {
        animation = ObjectAnimator.ofInt(target, PROGRESS_BAR_PROPERTY, start, end).apply {
            this.interpolator = progressInterpolator
            this.duration = duration
            start()
        }
    }

    fun cancelAnimation() {
        if (animation?.isStarted == true) {
            animation?.cancel()
            animation = null
        }
    }
}