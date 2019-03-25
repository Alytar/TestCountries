package com.testcountriesapp.repository.remote.error

class CommonThrowable(
    message: String,
    val connectionError: Boolean,
    val errorCode: Int? = 0,
    val errors: Map<String, String>? = null,
    cause: Throwable? = null
) : Throwable(message, cause)