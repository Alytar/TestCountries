package com.testcountriesapp.repository.remote.error

import android.util.Log
import androidx.annotation.StringRes
import com.crashlytics.android.Crashlytics
import com.testcountriesapp.BuildConfig
import com.testcountriesapp.R
import com.testcountriesapp.TestCountriesApp
import com.testcountriesapp.general.CrashlyticsCrashReportingTree
import io.reactivex.exceptions.CompositeException
import io.reactivex.functions.Consumer
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class GeneralErrorHandler(private val onFailure: ((CommonThrowable) -> Unit)? = null) : Consumer<Throwable> {

    private fun isNetworkError(throwable: Throwable) =
        throwable is SocketException || throwable is UnknownHostException || throwable is SocketTimeoutException

    override fun accept(throwable: Throwable) {
        reportError(throwable)

        val error = when {
            isNetworkError(throwable) -> CommonThrowable(getString(R.string.general_error_handler_server_error), true)
            throwable is NoNetworkException -> CommonThrowable(throwable.message!!, true)
            throwable is CompositeException -> {
                for (exception in throwable.exceptions) {
                    Timber.e(exception)
                }
                CommonThrowable(getString(R.string.general_error_handler_unknown_error), false, cause = throwable)
            }
            throwable is ApiException -> CommonThrowable(
                throwable.errorMessage ?: getString(R.string.general_error_handler_unknown_error)
                , false,
                throwable.errorCode,
                errors = throwable.errors
            )
            throwable is HttpException -> {
                CommonThrowable(getString(R.string.general_error_handler_unknown_error), true)
            }
            else -> CommonThrowable(getString(R.string.general_error_handler_unknown_error), false, cause = throwable)
        }
        onFailure?.invoke(error)
    }

    private fun reportError(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            Timber.e(throwable)
        }

        Crashlytics.setInt(CrashlyticsCrashReportingTree.CRASHLYTICS_KEY_PRIORITY, Log.WARN)
        Crashlytics.setString(CrashlyticsCrashReportingTree.CRASHLYTICS_KEY_TAG, "GeneralErrorHandler report")
        Crashlytics.setString(CrashlyticsCrashReportingTree.CRASHLYTICS_KEY_MESSAGE, throwable.message)
        Crashlytics.logException(throwable)
    }

    private fun getString(@StringRes resId: Int) = TestCountriesApp.applicationContext().getString(resId)
}