package com.flux.data.model

import androidx.room.TypeConverter

class Converter {
    @TypeConverter
    fun fromStringList(list: List<String>): String = list.joinToString(separator = ",")

    @TypeConverter
    fun toStringList(data: String): List<String> =
        if (data.isBlank()) emptyList() else data.split(",")
}
