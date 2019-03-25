package com.testcountriesapp.util

import greyfox.rxnetwork.internal.strategy.internet.impl.HttpOkInternetObservingStrategy

object NetworkObservableStrategy {

    fun getStrategy() = HttpOkInternetObservingStrategy.builder()
        .endpoint("https://www.google.com/blank.html")
        .delay(1000)
        .interval(5000)
        .build()
}