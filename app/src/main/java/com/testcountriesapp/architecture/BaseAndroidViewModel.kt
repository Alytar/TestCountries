package com.testcountriesapp.architecture

import android.app.Application
import androidx.annotation.CallSuper
import androidx.lifecycle.*
import com.testcountriesapp.general.extension.bind
import com.testcountriesapp.general.liveData.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

open class BaseAndroidViewModel(app: Application) : AndroidViewModel(app), LifecycleObserver {

    protected val disposables = CompositeDisposable()
    val progressLoadingEvent = MutableLiveData<Boolean>()
    val hasInternetConnectionEvent = MutableLiveData<Boolean>()
    val errorDialogEvent = SingleLiveEvent<Triple<String?, (() -> Unit)?, (() -> Unit)?>>()
    var isAppForeground = false
        private set(value) {
            field = value
            Timber.d("${this::class.java.simpleName}> isAppForeground $value")
        }

    init {
        progressLoadingEvent.postValue(false)
    }

    protected fun showLoading() {
        progressLoadingEvent.postValue(true)
    }

    fun hideLoading() {
        progressLoadingEvent.postValue(false)
    }

    fun bindDisposable(disposable: Disposable?) = disposable?.bind(disposables)

    protected fun showError(
        message: String?,
        cancelListener: (() -> Unit)? = null,
        retryListener: (() -> Unit)? = null
    ) {
        message?.let { Timber.e(it) }
        errorDialogEvent.postValue(Triple(message, cancelListener, retryListener))
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        Timber.d("${this::class.java.simpleName}> onCleared()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        isAppForeground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        isAppForeground = false
    }
}