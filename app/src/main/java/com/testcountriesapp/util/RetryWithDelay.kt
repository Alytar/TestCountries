package com.testcountriesapp.util

import io.reactivex.Observable
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

class RetryWithDelay(
    private val maxRetries: Int,
    private var delay: Long,
    private val delayAmount: Long = 100L
) : Function<Observable<out Throwable>, Observable<*>> {

    private var retryCount = 0

    override fun apply(attempts: Observable<out Throwable>): Observable<*> {
        return attempts.flatMap {
            if (++retryCount < maxRetries) {
                delay += delayAmount
                Observable.timer(delay, TimeUnit.MILLISECONDS)
            } else {
                Observable.error(it)
            }
        }
    }
}