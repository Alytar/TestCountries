package com.testcountriesapp.repository.remote.serializer

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class DateTimeSerializer(private val pattern: String) : JsonSerializer<Date> {

    private val patternDateFormat: SimpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())

    override fun serialize(src: Date, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return if (pattern.isEmpty()) {
            context.serialize(src.time / 1000L) // all date in app in seconds // todo change if millis
        } else {
            context.serialize(patternDateFormat.format(src))
        }
    }
}
