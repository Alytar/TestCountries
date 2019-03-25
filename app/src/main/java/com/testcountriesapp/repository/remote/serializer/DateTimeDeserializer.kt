package com.testcountriesapp.repository.remote.serializer

import android.text.TextUtils
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat
import timber.log.Timber
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.util.*

class DateTimeDeserializer(private val pattern: String) : JsonDeserializer<Date> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date? {

        val date = json.asJsonPrimitive.asString
        if (TextUtils.isEmpty(date)) {
            return null
        }

        if (pattern.isNotEmpty()) {
            val predefinedFormat = DateTimeFormat.forPattern(pattern)
            try {
                val dateTime = predefinedFormat.parseDateTime(date)
                return dateTime.toDate()
            } catch (e: ParseException) {
                // ignore and try next
            }
        }

        if (TextUtils.isDigitsOnly(date)) {
            val dateLong = date.toLong()
            return Date(dateLong * 1000L) // all date in app in seconds // todo change if millis
        }

        // try parse via stock DateFormat class
        try {
            val dateFormat = DateFormat.getInstance()
            return dateFormat.parse(date)
        } catch (e: ParseException) {
            // ignore and try next
        }

        // try parse yyyy-mm-dd format
        try {
            val dateTime = mDateOnlyFormat.parseDateTime(date)
            return dateTime.toDate()
        } catch (e: Exception) {
            // ignore and try next
        }

        // try parse yyyy-mm-dd hh:mm:ss format
        try {
            val dateTime = mDateAndTimeFormat.parseDateTime(date)
            return dateTime.toDate()
        } catch (e: Exception) {
            // ignore and try next
        }

        // try parse yyyy-mm-dd hh:mm format
        try {
            val dateTime = mDateAndTimeFormat1.parseDateTime(date)
            return dateTime.toDate()
        } catch (e: Exception) {
            // ignore and try next
        }

        // try parse dd/MM/yyyy format
        try {
            val dateTime = mDateOnlySlashedFormat.parseDateTime(date)
            return dateTime.toDate()
        } catch (e: Exception) {
            // ignore and try next
        }

        // try parse ISO date
        try {
            val dateTime = mISOWithoutMillisOrOffset.parseDateTime(date)
            return dateTime.toDate()
        } catch (e: Exception) {
            // ignore and try next
        }

        // try parse yyyy-MM-dd'T'HH:mm:ssZ format
        try {
            val dateTime = mISOWithoutMillisOrOffsetWithTimezone.parseDateTime(date)
            return dateTime.toDate()
        } catch (e: Exception) {
            // ignore and try next
        }

        // try parse ISO date
        try {
            val dateTime = ISODateTimeFormat.dateTimeParser()
                .withOffsetParsed()
                .parseDateTime(date)
            return dateTime.toDate()
        } catch (e: Exception) {
            Timber.e("Error parsing date")
        }

        return null
    }

    companion object {
        private val mISOWithoutMillisOrOffsetWithTimezone = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
        private val mISOWithoutMillisOrOffset = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")
        private val mDateAndTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
        private val mDateAndTimeFormat1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")
        private val mDateOnlyFormat = DateTimeFormat.forPattern("yyyy-MM-dd")
        private val mDateOnlySlashedFormat = DateTimeFormat.forPattern("dd/MM/yyyy")
    }
}
