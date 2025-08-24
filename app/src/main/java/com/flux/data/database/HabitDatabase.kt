package com.flux.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flux.data.dao.HabitsDao
import com.flux.data.model.Converter
import com.flux.data.model.HabitModel

@Database(entities = [HabitModel::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract val dao: HabitsDao
}
