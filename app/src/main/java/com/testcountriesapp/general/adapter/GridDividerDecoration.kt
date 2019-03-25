package com.testcountriesapp.general.adapter

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridDividerDecoration(private val divider: Drawable) : RecyclerView.ItemDecoration() {

    private val insets: Int = 0

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawVertical(c, parent)
        drawHorizontal(c, parent)
    }

    /**
     * Draw dividers at each expected grid interval
     */
    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        if (parent.childCount == 0) return

        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val left = child.left - params.leftMargin - insets / 2
            val right = child.right + params.rightMargin + insets / 2
            val top = child.bottom + params.bottomMargin - insets//remove top line
            val bottom = top + divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }

    /**
     * Draw dividers to the right of each child view
     */
    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val left = child.right + params.rightMargin + insets / 2
            val right = left + divider.intrinsicWidth
            val top = child.top - params.topMargin - insets
            val bottom = child.bottom + params.bottomMargin - insets
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        //We can supply forced insets for each item view here in the Rect
        outRect.set(insets, insets, insets, insets)
        super.getItemOffsets(outRect, view, parent, state)
    }
}