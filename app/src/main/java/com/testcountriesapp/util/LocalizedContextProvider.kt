package com.testcountriesapp.util

import android.content.Context
import java.util.*

object LocalizedContextProvider {

    fun getLocalizedContext(context: Context): Context = Locale("en").let {
        Locale.setDefault(it)
        context.createConfigurationContext(context.resources.configuration.apply {
            setLocale(it)
            setLayoutDirection(it)
        })
    }
}