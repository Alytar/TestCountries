package com.testcountriesapp.repository.remote.error

class ApiException(
    val errorCode: Int? = -1,
    val errorMessage: String? = null,
    val errors: Map<String, String>?
) : RuntimeException() {

    companion object {
        const val ERROR_CODE_NO_ERRORS = 0
    }

    override fun toString() =
        "[errorCode: $errorCode],\n[errorMessage: $errorMessage],\n[errors: ${errors.toString()}]"
}