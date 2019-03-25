package com.testcountriesapp.util

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator

class Transitioner(startingView: View, endingView: View) {

    var interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()
    var duration = 400
        set(value) {
            if (value >= 0) field = value
        }
    var currentProgress = 0f
        private set

    private var mappedViews: ArrayList<StateOfViews> = arrayListOf()
    private var startingChildViews = getAllChildren(startingView)
    private var endingChildViews = getAllChildren(endingView)
    private var onPercentChanged: (percent: Float) -> Unit = {}

    init {
        startingChildViews.forEach { old ->
            endingChildViews
                .filter { old.tag == it.tag }
                .forEach {
                    mappedViews.add(
                        StateOfViews(
                            old,
                            it,
                            Dimensions(old.x.toInt(), old.y.toInt(), old.width, old.height)
                        )
                    )
                }
        }
    }

    fun setProgress(progress: Float) {
        currentProgress = progress
        onPercentChanged(progress)
        mappedViews.forEach {
            it.apply {
                startV.x = origDimens.x + (endV.x - origDimens.x) * progress
                startV.y = origDimens.y + (endV.y - origDimens.y) * progress
                startV.layoutParams.width = origDimens.width + ((endV.width - origDimens.width) * progress).toInt()
                startV.layoutParams.height = origDimens.height + ((endV.height - origDimens.height) * progress).toInt()
                startV.requestLayout()
            }
        }
    }

    fun animateTo(percent: Float, duration: Long? = null, interpolator: TimeInterpolator? = null) {
        if (currentProgress == percent || percent < 0f || percent > 1f) return
        ValueAnimator.ofFloat(currentProgress, percent).apply {
            this.duration = duration ?: this@Transitioner.duration.toLong()
            this.interpolator = interpolator ?: this@Transitioner.interpolator
            addUpdateListener { animation ->
                setProgress(animation.animatedValue as Float)
            }
            start()
        }
    }

    fun onProgressChanged(func: (percent: Float) -> Unit) {
        this.onPercentChanged = func
    }

    fun setProgress(percent: Int) = setProgress(percent.toFloat() / 100)

    private fun getAllChildren(v: View): ArrayList<View> {
        val visited = ArrayList<View>()
        val unvisited = ArrayList<View>()
        unvisited.add(v)
        while (!unvisited.isEmpty()) {
            val child = unvisited.removeAt(0)
            visited.add(child)
            if (child !is ViewGroup) continue
            (0 until child.childCount).mapTo(unvisited) { child.getChildAt(it) }
        }
        return visited
    }

    private data class StateOfViews(var startV: View, var endV: View, var origDimens: Dimensions)

    private data class Dimensions(var x: Int, var y: Int, var width: Int, var height: Int)
}