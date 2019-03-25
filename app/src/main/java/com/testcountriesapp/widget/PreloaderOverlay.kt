package com.testcountriesapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import com.testcountriesapp.R
import com.testcountriesapp.general.extension.gone
import com.testcountriesapp.general.extension.inflate
import com.testcountriesapp.general.extension.visible
import com.testcountriesapp.util.AnimationHelper

class PreloaderOverlay @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, style: Int = 0) :
    FrameLayout(context, attrs, style) {

    private val alphaInAnimation =
        AnimationHelper.getAlphaAnimation(
            this,
            AnimationHelper.ALPHA_MIN,
            AnimationHelper.ALPHA_MAX,
            ANIMATION_DURATION,
            AccelerateInterpolator(),
            doOnStart = {
                visible()
            }
        )
    private val alphaOutAnimation =
        AnimationHelper.getAlphaAnimation(
            this,
            AnimationHelper.ALPHA_MAX,
            AnimationHelper.ALPHA_MIN,
            ANIMATION_DURATION,
            AccelerateInterpolator(),
            doOnEnd = {
                gone()
            }
        )

    var visible = false
        set(value) {
            field = value
            if (field) {
                alphaOutAnimation.cancel()
                alphaInAnimation.start()
            } else {
                alphaInAnimation.cancel()
                alphaOutAnimation.start()
            }
        }

    init {
        gone()
        inflate(R.layout.widget_preloader_overlay, true)
        alpha = AnimationHelper.ALPHA_MIN
        translationZ = OVERLAY_DEFAULT_TRANSLATION_Z
    }

    companion object {

        private const val ANIMATION_DURATION = 250L
        private const val OVERLAY_DEFAULT_TRANSLATION_Z = 100f
    }
}