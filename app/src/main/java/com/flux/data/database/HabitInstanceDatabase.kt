package com.flux.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flux.data.dao.HabitInstanceDao
import com.flux.data.model.Converter
import com.flux.data.model.HabitInstanceModel


@Database(entities = [HabitInstanceModel::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class HabitInstanceDatabase : RoomDatabase() {
    abstract val dao: HabitInstanceDao
}