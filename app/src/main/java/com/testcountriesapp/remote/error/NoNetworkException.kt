package com.testcountriesapp.remote.error

import com.testcountriesapp.R
import com.testcountriesapp.TestCountriesApp


class NoNetworkException : Exception(TestCountriesApp.applicationContext().getString(R.string.message_no_internet))
