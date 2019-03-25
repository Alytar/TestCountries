package com.testcountriesapp.general.extension

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(layoutRes: Int, attach: Boolean = false): View = getInflater().inflate(layoutRes, this, attach)

fun ViewGroup.getInflater(): LayoutInflater = LayoutInflater.from(context)

fun ViewGroup.getViewByCoordinates(x: Float, y: Float): View? {
    (0 until this.childCount)
        .map { this.getChildAt(it) }
        .forEach {
            val bounds = Rect()
            it.getHitRect(bounds)
            if (bounds.contains(x.toInt(), y.toInt())) {
                return it
            }
        }
    return null
}