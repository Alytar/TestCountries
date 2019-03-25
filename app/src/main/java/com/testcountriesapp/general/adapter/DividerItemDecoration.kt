package com.testcountriesapp.general.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DividerItemDecoration : RecyclerView.ItemDecoration {

    private var divider: Drawable? = null
    private var showFirstDivider = false
    private var showLastDivider = false
    private var countTopDividersDontShow = 0
    private var orientation = -1

    constructor(context: Context, attrs: AttributeSet) {
        val a = context
            .obtainStyledAttributes(attrs, intArrayOf(android.R.attr.listDivider))
        divider = a.getDrawable(0)
        a.recycle()
    }

    constructor(
        context: Context, attrs: AttributeSet, showFirstDivider: Boolean,
        showLastDivider: Boolean
    ) : this(context, attrs) {
        this.showFirstDivider = showFirstDivider
        this.showLastDivider = showLastDivider
    }

    constructor(context: Context, resId: Int) {
        divider = ContextCompat.getDrawable(context, resId)
    }

    constructor(
        context: Context, resId: Int, showFirstDivider: Boolean,
        showLastDivider: Boolean
    ) : this(context, resId) {
        this.showFirstDivider = showFirstDivider
        this.showLastDivider = showLastDivider
    }

    constructor(divider: Drawable) {
        this.divider = divider
    }

    constructor(
        divider: Drawable, showFirstDivider: Boolean,
        showLastDivider: Boolean
    ) : this(divider) {
        this.showFirstDivider = showFirstDivider
        this.showLastDivider = showLastDivider
    }

    constructor(divider: Drawable, countTopDividersDontShow: Int) : this(divider) {
        this.countTopDividersDontShow = countTopDividersDontShow
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (divider == null) {
            return
        }

        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION || (position == 0 || position == 1) && !showFirstDivider) {
            return
        }

        if (orientation == -1)
            getOrientation(parent)

        if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.top = divider!!.intrinsicHeight
            if (showLastDivider && position == state.itemCount - 1) {
                outRect.bottom = outRect.top
            }
        } else {
            outRect.left = divider!!.intrinsicWidth
            if (showLastDivider && position == state.itemCount - 1) {
                outRect.right = outRect.left
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (divider == null) {
            super.onDrawOver(c, parent, state)
            return
        }

        // Initialization needed to avoid compiler warning
        var left = 0
        var right = 0
        var top = 0
        var bottom = 0
        val size: Int
        val orientation = if (orientation != -1) orientation else getOrientation(parent)
        val childCount = parent.childCount

        if (orientation == LinearLayoutManager.VERTICAL) {
            size = divider!!.intrinsicHeight
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
        } else { //horizontal
            size = divider!!.intrinsicWidth
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
        }

        val count: Int
        if (countTopDividersDontShow != 0) {
            count = countTopDividersDontShow
        } else {
            count = if (showFirstDivider) 0 else 1
        }

        for (i in count until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            if (orientation == LinearLayoutManager.VERTICAL) {
                top = child.top - params.topMargin - size
                bottom = top + size
            } else { //horizontal
                left = child.left - params.leftMargin
                right = left + size
            }
            divider!!.setBounds(left, top, right, bottom)
            divider!!.draw(c)
        }

        // show last divider
        if (showLastDivider && childCount > 0) {
            val child = parent.getChildAt(childCount - 1)
            if (parent.getChildAdapterPosition(child) == state.itemCount - 1) {
                val params = child.layoutParams as RecyclerView.LayoutParams
                if (orientation == LinearLayoutManager.VERTICAL) {
                    top = child.bottom + params.bottomMargin
                    bottom = top + size
                } else { // horizontal
                    left = child.right + params.rightMargin
                    right = left + size
                }
                divider!!.setBounds(left, top, right, bottom)
                divider!!.draw(c)
            }
        }
    }

    private fun getOrientation(parent: RecyclerView): Int {
        if (orientation == -1) {
            if (parent.layoutManager is LinearLayoutManager) {
                val layoutManager = parent.layoutManager as LinearLayoutManager
                orientation = layoutManager.orientation
            } else {
                throw IllegalStateException(
                    "DividerItemDecoration can only be used with a LinearLayoutManager."
                )
            }
        }
        return orientation
    }
}