package com.testcountriesapp.architecture

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.testcountriesapp.general.Navigator
import com.testcountriesapp.general.behavior.ActionBarBehaviorWrapper
import com.testcountriesapp.general.behavior.IBehavior
import com.testcountriesapp.general.extension.hideKeyboard
import com.testcountriesapp.general.extension.isLazyInitialized
import com.tbruyelle.rxpermissions2.RxPermissions

abstract class BaseFragment(private val layoutResourceId: Int) : Fragment() {

    private val behaviors = mutableListOf<IBehavior?>()
    protected var navigator: Navigator? = null
    protected var actionBarBehavior: ActionBarBehaviorWrapper? = null
    val rxPermissions: RxPermissions? by lazy { RxPermissions(this) }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val baseActivity = activity as BaseActivity
        baseActivity.let { navigator = it.navigator }
        return if (layoutResourceId > 0) {
            inflater.inflate(layoutResourceId, container, false)
        } else {
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionBarBehavior = (activity as BaseActivity).actionBarBehavior
        actionBarBehavior?.let { initToolbar(it) }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser && isResumed) {
            actionBarBehavior?.let { initToolbar(it) }
        }
    }

    abstract fun initToolbar(behavior: ActionBarBehaviorWrapper)

    protected fun <T : IBehavior> attachBehavior(behavior: T) = behavior.also {
        behaviors.add(it)
    }

    protected fun setInProgress(inProgress: Boolean) {
        activity?.let {
            (it as BaseActivity).setInProgress(inProgress)
        }
    }

    protected fun showErrorDialog(errorData: Triple<String?, (() -> DialogInterface.OnClickListener?)?, (() -> DialogInterface.OnClickListener?)?>) {
        activity?.let {
            (it as BaseActivity).showErrorDialog(errorData)
        }
    }

    protected fun hideKeyboard() {
        activity?.hideKeyboard()
    }

    @CallSuper
    override fun onDetach() {
        behaviors.forEach { it?.detach() }
        behaviors.clear()
        navigator = null
        super.onDetach()
    }
}
