package com.testcountriesapp.architecture

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import com.testcountriesapp.R
import com.testcountriesapp.general.extension.getFragmentTag
import com.testcountriesapp.general.extension.getInputMethodManager
import timber.log.Timber

abstract class BaseDialogFragment(@StyleRes private val dialogAnimationStyle: Int = R.style.FadeDialogAnimation) :
    AppCompatDialogFragment() {

    fun show(manager: FragmentManager) {
        if (isAdded) {
            return
        }
        try {
            super.show(manager, getFragmentTag())
        } catch (exception: Exception) {
            Timber.e(exception, "Show dialog error: %s", getFragmentTag())
        }
    }

    protected fun hideKeyboard() {
        val view: View? = dialog?.currentFocus
        if (view != null) {
            context?.getInputMethodManager()
                ?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onActivityCreated(args: Bundle?) {
        super.onActivityCreated(args)
        if (dialogAnimationStyle != 0) {
            dialog?.window?.attributes?.windowAnimations = dialogAnimationStyle
        }
    }
}
