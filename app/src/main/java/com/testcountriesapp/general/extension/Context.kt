package com.testcountriesapp.general.extension

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.testcountriesapp.BuildConfig

fun Context.getCompatColor(@ColorRes colorRes: Int): Int = ContextCompat.getColor(this, colorRes)

inline fun Context.debug(code: () -> Unit) {
    if (BuildConfig.DEBUG) {
        code()
    }
}

fun Context.toast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

fun Context.toast(resId: Int, duration: Int = Toast.LENGTH_LONG) {
    val toast = Toast.makeText(this, resId, duration)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}

fun Context.convertDpToPixel(dp: Int): Int {
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.hasNetworkConnection(): Boolean {
    val activeNetworkInfo = getConnectivityManager().activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun Context.intentSafe(intent: Intent): Boolean {
    val activities = packageManager
        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return activities.isNotEmpty()
}