package com.flux.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.util.Date

class Converter {
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

    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return value.joinToString(separator = ",")
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        return if (value.isEmpty()) emptyList()
        else value.split(",").map { it.toLong() }
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

}
