package com.testcountriesapp.general.extension

import android.content.Context
import android.util.Base64
import android.util.TypedValue
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

fun Int.toDp(context: Context) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)

fun Boolean?.falseIfNull() = this ?: false

fun Boolean?.trueIfNull() = this ?: true

fun Boolean?.toInt() = if (this == true) 1 else 0

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun String.decode64(): String =
    Base64.decode(this, Base64.DEFAULT)
        .toString(StandardCharsets.UTF_8)


fun String.encode64(): String =
    Base64.encodeToString(this.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)
        .replace("\n", "")