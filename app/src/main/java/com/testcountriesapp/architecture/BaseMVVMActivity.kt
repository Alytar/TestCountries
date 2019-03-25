package com.testcountriesapp.architecture

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

abstract class BaseMVVMActivity(
    @LayoutRes layoutResourceId: Int = 0,
    @IdRes baseFragmentLayout: Int = 0
) : BaseActivity(layoutResourceId, baseFragmentLayout) {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        onBindLiveData()
        super.onCreate(savedInstanceState)
        attachLifecycleObserver()
    }

    /**
     * Attach viewModel to observe activity lifecycle
     */
    abstract fun attachLifecycleObserver()

    /**
     * Here we may bind our observers to LiveData if some.
     * This method will be executed after parent [onCreate] method
     */
    protected open fun onBindLiveData() {
        //Optional
    }

    fun <T, LD : LiveData<T>> observeNullable(liveData: LD, onChanged: (T?) -> Unit) {
        liveData.observe(this, Observer { value ->
            onChanged(value)
        })
    }

    fun <T, LD : LiveData<T>> observe(liveData: LD, onChanged: (T) -> Unit) {
        liveData.observe(this, Observer { value ->
            value?.let(onChanged)
        })
    }
}