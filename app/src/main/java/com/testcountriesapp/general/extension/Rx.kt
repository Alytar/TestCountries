package com.testcountriesapp.general.extension

import com.testcountriesapp.general.EmptyConsumer
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

fun <T> Observable<T>.subscribeEmpty(consumer: Consumer<T>? = EmptyConsumer()): Disposable =
    subscribe(consumer, EmptyConsumer())

fun <T> Flowable<T>.subscribeEmpty(consumer: Consumer<T>? = EmptyConsumer()): Disposable =
    subscribe(consumer, EmptyConsumer())

fun <T> Single<T>.subscribeEmpty(consumer: Consumer<T>? = EmptyConsumer()): Disposable =
    subscribe(consumer, EmptyConsumer())

fun Disposable.bind(compositeSubscription: CompositeDisposable): Disposable {
    compositeSubscription.add(this)
    return this
}
