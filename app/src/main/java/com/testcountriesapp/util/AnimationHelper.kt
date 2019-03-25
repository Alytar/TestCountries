package com.testcountriesapp.util

import android.animation.*
import android.graphics.PorterDuff
import android.view.View
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.transition.Transition
import com.testcountriesapp.general.extension.toInt
import com.testcountriesapp.general.extension.visible

object AnimationHelper {

    const val ALPHA_PROPERTY_NAME = "alpha"
    const val TRANSLATION_Y_PROPERTY_NAME = "translationY"
    const val TRANSLATION_X_PROPERTY_NAME = "translationX"
    const val TEXT_SIZE_PROPERTY_NAME = "textSize"
    const val SCALE_X_PROPERTY_NAME = "scaleX"
    const val SCALE_Y_PROPERTY_NAME = "scaleY"
    const val ALPHA_MIN = 0f
    const val ALPHA_MAX = 1f
    const val TRANSLATION_MIN = 0f
    private const val HALF_PIVOT_VALUE = 0.5f
    private const val DEFAULT_DURATION = 0L

    fun getAlphaAnimation(
        target: View,
        fromValue: Float,
        toValue: Float,
        duration: Long = DEFAULT_DURATION,
        interpolator: Interpolator? = null,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ): Animator = ObjectAnimator.ofFloat(target, ALPHA_PROPERTY_NAME, fromValue, toValue)
        .apply {
            this.interpolator = interpolator
            if (duration != DEFAULT_DURATION) this.duration = duration
            if (doOnStart != null || doOnEnd != null || doOnCancel != null) addListener(
                getDefaultAnimatorListener(
                    doOnStart,
                    doOnEnd,
                    doOnCancel
                )
            )
        }

    fun getTranslateYAnimation(
        target: View,
        fromValue: Float,
        toValue: Float,
        duration: Long = DEFAULT_DURATION,
        interpolator: Interpolator? = null,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ): Animator = ObjectAnimator.ofFloat(target, TRANSLATION_Y_PROPERTY_NAME, fromValue, toValue)
        .apply {
            this.interpolator = interpolator
            if (duration != DEFAULT_DURATION) this.duration = duration
            if (doOnStart != null || doOnEnd != null || doOnCancel != null) addListener(
                getDefaultAnimatorListener(
                    doOnStart,
                    doOnEnd,
                    doOnCancel
                )
            )
        }

    fun getScaleAnimatorSet(
        target: View,
        fromValue: Float,
        toValue: Float,
        duration: Long = DEFAULT_DURATION,
        interpolator: Interpolator? = null,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ): Animator = getDefaultAnimatorSet(duration, interpolator).apply {
        val scaleXAnimator = ObjectAnimator.ofFloat(target, SCALE_X_PROPERTY_NAME, fromValue, toValue)
        val scaleYAnimator = ObjectAnimator.ofFloat(target, SCALE_Y_PROPERTY_NAME, fromValue, toValue)
        playTogether(scaleXAnimator, scaleYAnimator)
        if (doOnStart != null || doOnEnd != null || doOnCancel != null) addListener(
            getDefaultAnimatorListener(
                doOnStart,
                doOnEnd,
                doOnCancel
            )
        )
    }

    fun getRotationAnimation(
        fromValue: Float,
        toValue: Float,
        duration: Long,
        infiniteRepeat: Boolean,
        interpolator: Interpolator? = null
    ): Animation = RotateAnimation(
        fromValue,
        toValue,
        Animation.RELATIVE_TO_SELF,
        HALF_PIVOT_VALUE,
        Animation.RELATIVE_TO_SELF,
        HALF_PIVOT_VALUE
    )
        .apply {
            this.duration = duration
            this.interpolator = interpolator
            if (infiniteRepeat) this.repeatCount = Animation.INFINITE
        }

    fun getAppearAnimatorSet(
        target: View,
        fromAlphaValue: Float,
        fromTransitionValue: Float,
        duration: Long = DEFAULT_DURATION,
        interpolator: Interpolator? = null,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ): AnimatorSet = getDefaultAnimatorSet(duration, interpolator, doOnStart, doOnEnd, doOnCancel)
        .apply {
            val alphaAnimator = getAlphaAnimation(target, fromAlphaValue, ALPHA_MAX, duration)
            val translationYAnimator = getTranslateYAnimation(target, fromTransitionValue, TRANSLATION_MIN, duration)
            playTogether(alphaAnimator, translationYAnimator)
        }

    fun getTextColorChangeAnimator(
        textView: TextView,
        @ColorRes fromColor: Int,
        @ColorRes toColor: Int,
        duration: Long = DEFAULT_DURATION,
        interpolator: Interpolator? = null,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ): ValueAnimator = ValueAnimator.ofObject(
        ArgbEvaluator(),
        ContextCompat.getColor(textView.context, fromColor),
        ContextCompat.getColor(textView.context, toColor)
    )
        .apply {
            this.interpolator = interpolator
            addUpdateListener { animator -> textView.setTextColor(animator.animatedValue as Int) }
            if (doOnStart != null || doOnEnd != null || doOnCancel != null) addListener(
                getDefaultAnimatorListener(
                    doOnStart,
                    doOnEnd,
                    doOnCancel
                )
            )
            if (duration != DEFAULT_DURATION) this.duration = duration
        }

    fun getImageTintColorChangeAnimator(
        view: ImageView,
        fromColor: Int,
        toColor: Int,
        duration: Long = DEFAULT_DURATION,
        interpolator: Interpolator? = null,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ): ValueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        .apply {
            this.interpolator = interpolator
            addUpdateListener { animator -> view.setColorFilter(animator.animatedValue as Int, PorterDuff.Mode.DST_IN) }
            if (doOnStart != null || doOnEnd != null || doOnCancel != null) addListener(
                getDefaultAnimatorListener(
                    doOnStart,
                    doOnEnd,
                    doOnCancel
                )
            )
            if (duration != DEFAULT_DURATION) this.duration = duration
        }

    fun getColorChangeAnimator(
        fromColor: Int,
        toColor: Int,
        duration: Long = DEFAULT_DURATION,
        interpolator: Interpolator? = null,
        doOnUpdate: ((Int) -> Unit)? = null,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ): ValueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        .apply {
            this.interpolator = interpolator
            if (doOnUpdate != null) addUpdateListener { animator -> doOnUpdate.invoke(animator.animatedValue.toInt()) }
            if (doOnStart != null || doOnEnd != null || doOnCancel != null) addListener(
                getDefaultAnimatorListener(
                    doOnStart,
                    doOnEnd,
                    doOnCancel
                )
            )
            if (duration != DEFAULT_DURATION) this.duration = duration
        }

    fun getGroupAppearAnimatorSet(
        vararg views: View?,
        fromAlphaValue: Float,
        fromTransitionValue: Float,
        playTogether: Boolean,
        duration: Long = DEFAULT_DURATION,
        startDelay: Long = DEFAULT_DURATION,
        interpolator: Interpolator? = null,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ) = getDefaultAnimatorSet(
        duration,
        interpolator,
        doOnStart = {
            views.filter { it != null }
                .forEach {
                    it?.alpha = AnimationHelper.ALPHA_MIN
                    it?.translationY = fromTransitionValue
                    it?.visible()
                }
            doOnStart?.invoke()
        },
        doOnEnd = doOnEnd,
        doOnCancel = doOnCancel
    ).apply {
        val animators = views.filter { it != null }
            .map {
                AnimationHelper.getAppearAnimatorSet(
                    it!!,
                    fromAlphaValue,
                    fromTransitionValue
                )
            }
        if (startDelay != DEFAULT_DURATION) this.startDelay = startDelay
        if (playTogether) playTogether(animators) else playSequentially(animators)
    }

    fun getShakerAnimator(
        target: View,
        duration: Long = DEFAULT_DURATION,
        interpolator: Interpolator? = null,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ): Animator =
        ObjectAnimator.ofFloat(target, TRANSLATION_X_PROPERTY_NAME, 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
            .apply {
                if (duration != DEFAULT_DURATION) this.duration = duration
                this.interpolator = interpolator
                if (doOnStart != null || doOnEnd != null || doOnCancel != null) addListener(
                    getDefaultAnimatorListener(
                        doOnStart,
                        doOnEnd,
                        doOnCancel
                    )
                )
            }

    fun getTextSizeAnimator(
        target: TextView,
        fromValue: Float,
        toValue: Float,
        duration: Long = DEFAULT_DURATION,
        interpolator: Interpolator? = null,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ): Animator = ObjectAnimator.ofFloat(target, TEXT_SIZE_PROPERTY_NAME, fromValue, toValue)
        .apply {
            if (duration != DEFAULT_DURATION) this.duration = duration
            this.interpolator = interpolator
            if (doOnStart != null || doOnEnd != null || doOnCancel != null) addListener(
                getDefaultAnimatorListener(
                    doOnStart,
                    doOnEnd,
                    doOnCancel
                )
            )
        }

    fun getSlideRightLeftAnimation(
        duration: Long = DEFAULT_DURATION,
        interpolator: Interpolator? = null,
        isAppear: Boolean = true,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null
    ) = ScaleAnimation(
        if (isAppear) 0.0f else 1.0f,
        if (!isAppear) 0.0f else 1.0f,
        1.0f,
        1.0f,
        Animation.RELATIVE_TO_SELF, if (isAppear) 0.0f else 1.0f,
        Animation.RELATIVE_TO_SELF, 0.5f
    ).apply {
        if (duration != DEFAULT_DURATION) this.duration = duration
        this.interpolator = interpolator
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                doOnEnd?.invoke()
            }

            override fun onAnimationStart(animation: Animation?) {
                doOnStart?.invoke()
            }
        })
    }

    fun getDefaultAnimatorSet(
        duration: Long = 0L,
        interpolator: Interpolator? = null,
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ): AnimatorSet = AnimatorSet().apply {
        if (duration != DEFAULT_DURATION) this.duration = duration
        this.interpolator = interpolator
        if (doOnStart != null || doOnEnd != null || doOnCancel != null) addListener(
            getDefaultAnimatorListener(
                doOnStart,
                doOnEnd,
                doOnCancel
            )
        )
    }

    private fun getDefaultAnimatorListener(
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null
    ): Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            doOnEnd?.invoke()
        }

        override fun onAnimationCancel(animation: Animator?) {
            doOnCancel?.invoke()
        }

        override fun onAnimationStart(animation: Animator?) {
            doOnStart?.invoke()
        }
    }

    fun getDefaultTransactionListener(
        doOnStart: (() -> Unit)? = null,
        doOnEnd: (() -> Unit)? = null,
        doOnCancel: (() -> Unit)? = null,
        doOnResume: (() -> Unit)? = null,
        doOnPause: (() -> Unit)? = null
    ) = object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition) {
            doOnEnd?.invoke()
        }

        override fun onTransitionResume(transition: Transition) {
            doOnResume?.invoke()
        }

        override fun onTransitionPause(transition: Transition) {
            doOnPause?.invoke()
        }

        override fun onTransitionCancel(transition: Transition) {
            doOnCancel?.invoke()
        }

        override fun onTransitionStart(transition: Transition) {
            doOnStart?.invoke()
        }
    }
}