package com.flux.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flux.data.dao.TodoDao
import com.flux.data.model.Converter
import com.flux.data.model.TodoModel

@Database(entities = [TodoModel::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract val dao: TodoDao
}