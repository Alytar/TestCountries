package com.testcountriesapp.general.extension

import android.view.WindowManager
import android.widget.PopupWindow

fun PopupWindow.dimBehind() {
    val container = contentView.rootView
    val context = contentView.context
    val windowManager = context.getWindowManager()
    val layoutParams = container.layoutParams as WindowManager.LayoutParams
    layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
    layoutParams.dimAmount = 0.3f
    windowManager.updateViewLayout(container, layoutParams)
}

