package com.flux.data.model

import androidx.room.TypeConverter
import java.util.Date

class Converter {
    @TypeConverter
    fun fromIntegerList(list: List<Int>): String = list.joinToString(separator = ",")

    @TypeConverter
    fun toIntegerList(data: String): List<Int> = if (data.isBlank()) emptyList() else data.split(",").map { it.toInt() }

    // Convert Date to Long and back
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millis: Long?): Date? {
        return millis?.let { Date(it) }
    }
}
