package com.flux.data.model

import androidx.room.TypeConverter
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.time.LocalDate
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

    @TypeConverter
    fun fromRepetition(value: Repetition): String = value.name

    @TypeConverter
    fun toRepetition(value: String): Repetition = Repetition.valueOf(value)

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)

    @TypeConverter
    fun fromEventStatus(status: EventStatus): String = status.name

    @TypeConverter
    fun toEventStatus(value: String): EventStatus = EventStatus.valueOf(value)

    @TypeConverter
    fun fromTodoItemList(items: List<TodoItem>): String {
        return Gson().toJson(items)
    }

    @TypeConverter
    fun toTodoItemList(json: String): List<TodoItem> {
        val type = object : TypeToken<List<TodoItem>>() {}.type
        return Gson().fromJson(json, type)
    }

}
