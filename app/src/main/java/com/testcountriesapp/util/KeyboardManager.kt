package com.testcountriesapp.util

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import io.reactivex.Observable
import java.lang.ref.WeakReference

class KeyboardManager(activity: Activity) {

    private val activityWeekReference = WeakReference<Activity>(activity)

    fun status(): Observable<KeyboardStatus> = Observable.create<KeyboardStatus> { emitter ->
        activityWeekReference.get()?.findViewById<View>(android.R.id.content)?.also {
            val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
                val rect = Rect().apply { it.getWindowVisibleDisplayFrame(this) }
                val screenHeight = it.height
                val keypadHeight = screenHeight - rect.bottom
                if (keypadHeight > screenHeight * 0.15) {
                    emitter.onNext(KeyboardStatus.OPEN)
                } else {
                    emitter.onNext(KeyboardStatus.CLOSED)
                }
            }

            it.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

            emitter.setCancellable {
                it.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            }
        }
    }.distinctUntilChanged()

    enum class KeyboardStatus {
        OPEN, CLOSED
    }
}