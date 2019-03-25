package com.testcountriesapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import com.testcountriesapp.R
import com.testcountriesapp.general.extension.gone
import com.testcountriesapp.general.extension.inflate
import com.testcountriesapp.general.extension.visible
import com.testcountriesapp.util.AnimationHelper
import kotlinx.android.synthetic.main.widget_no_network.view.*


class NoNetworkView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, style: Int = 0) :
    FrameLayout(context, attrs, style) {

    private val alphaInAnimation =
        AnimationHelper.getAlphaAnimation(
            this,
            AnimationHelper.ALPHA_MIN,
            AnimationHelper.ALPHA_MAX,
            ANIMATION_DURATION
        )
    private val translationInAnimation =
        AnimationHelper.getTranslateYAnimation(
            this,
            TRANSLATION_START,
            TRANSLATION_END,
            ANIMATION_DURATION
        )
    private val scaleInAnimation =
        AnimationHelper.getScaleAnimatorSet(
            this,
            SCALE_MIN,
            SCALE_MAX,
            ANIMATION_DURATION
        )
    private val inAnimationSet =
        AnimationHelper.getDefaultAnimatorSet(
            ANIMATION_DURATION,
            DecelerateInterpolator(),
            doOnStart = {
                indicator_image_view.startAnimation(rotationAnimation)
                visible()
            }
        )
    private val alphaOutAnimation =
        AnimationHelper.getAlphaAnimation(
            this,
            AnimationHelper.ALPHA_MAX,
            AnimationHelper.ALPHA_MIN,
            ANIMATION_DURATION,
            AccelerateInterpolator()
        )
    private val translationOutAnimation =
        AnimationHelper.getTranslateYAnimation(
            this,
            TRANSLATION_END,
            TRANSLATION_START,
            ANIMATION_DURATION,
            AccelerateInterpolator()
        )
    private val scaleOutAnimation =
        AnimationHelper.getScaleAnimatorSet(
            this,
            SCALE_MAX,
            SCALE_MIN,
            ANIMATION_DURATION
        )
    private val outAnimationSet =
        AnimationHelper.getDefaultAnimatorSet(
            ANIMATION_DURATION,
            DecelerateInterpolator(),
            doOnEnd = {
                gone()
                indicator_image_view.clearAnimation()
            }
        )
    private val rotationAnimation =
        AnimationHelper.getRotationAnimation(
            DEGREES_MIN,
            DEGREES_MAX,
            ROTATION_DURATION,
            true,
            LinearInterpolator()
        )

    var visible = false
        set(value) {
            field = value
            if (field) {
                indicator_image_view.clearAnimation()
                outAnimationSet.cancel()
                inAnimationSet.start()
            } else {
                indicator_image_view.clearAnimation()
                inAnimationSet.cancel()
                outAnimationSet.start()
            }
        }

    init {
        gone()
        inflate(R.layout.widget_no_network, true) // check exception
        alpha = AnimationHelper.ALPHA_MIN
        translationY = TRANSLATION_START
        translationZ = OVERLAY_DEFAULT_TRANSLATION_Z

        inAnimationSet.playTogether(alphaInAnimation, translationInAnimation, scaleInAnimation)
        outAnimationSet.playTogether(alphaOutAnimation, translationOutAnimation, scaleOutAnimation)
    }

    companion object {

        private const val OVERLAY_DEFAULT_TRANSLATION_Z = 100f
        private const val ANIMATION_DURATION = 250L
        private const val ROTATION_DURATION = 800L
        private const val TRANSLATION_START = -100f
        private const val TRANSLATION_END = 0f
        private const val DEGREES_MIN = 0f
        private const val DEGREES_MAX = 359f
        private const val SCALE_MIN = 0.7f
        private const val SCALE_MAX = 1f
    }
}