package com.testcountriesapp.general.extension

import androidx.lifecycle.ViewModel
import com.testcountriesapp.BuildConfig

inline fun ViewModel.debug(code: () -> Unit) {
    if (BuildConfig.DEBUG) {
        code()
    }
}