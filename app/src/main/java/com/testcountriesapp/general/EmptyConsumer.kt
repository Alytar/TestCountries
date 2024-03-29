package com.testcountriesapp.general

import com.testcountriesapp.BuildConfig
import io.reactivex.functions.Consumer
import timber.log.Timber

class EmptyConsumer<T> : Consumer<T> {

    override fun accept(t: T?) {
        if (BuildConfig.DEBUG && t is Throwable) {
            Timber.w(t)
        }
    }
}