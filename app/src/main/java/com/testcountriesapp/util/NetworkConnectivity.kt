package com.testcountriesapp.util
import com.testcountriesapp.TestCountriesApp
import com.testcountriesapp.general.extension.hasNetworkConnection
import com.testcountriesapp.remote.error.NoNetworkException
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

object NetworkConnectivity {

    fun getStateFlowable(withException: Boolean = false): Flowable<Boolean> = Flowable.create({ emitter ->
        when {
            TestCountriesApp.applicationContext().hasNetworkConnection() -> emitter.onNext(true)
            withException -> emitter.onError(
                NoNetworkException()
            )
            else -> emitter.onNext(false)
        }
        emitter.onComplete()
    }, BackpressureStrategy.LATEST)

    fun getStateSingle(withException: Boolean = false): Single<Boolean> = Single.create { emitter ->
        when {
            TestCountriesApp.applicationContext().hasNetworkConnection() -> emitter.onSuccess(true)
            withException -> emitter.onError(
                NoNetworkException()
            )
            else -> emitter.onSuccess(false)
        }
    }

    fun getStateObservable(withException: Boolean = false): Observable<Boolean> = Observable.create { emitter ->
        when {
            TestCountriesApp.applicationContext().hasNetworkConnection() -> emitter.onNext(true)
            withException -> emitter.onError(
                NoNetworkException()
            )
            else -> emitter.onNext(false)
        }
        emitter.onComplete()
    }
}