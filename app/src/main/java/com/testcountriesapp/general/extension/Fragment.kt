@file:Suppress("unchecked_cast")

package com.testcountriesapp.general.extension

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.getFragmentTag(suffix: String? = null): String = this::class.java.simpleName + (suffix ?: "")

fun Fragment.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    activity?.toast(text, duration)
}

fun Fragment.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    activity?.toast(resId, duration)
}