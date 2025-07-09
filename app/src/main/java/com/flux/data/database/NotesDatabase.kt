package com.flux.data.database

import com.flux.data.dao.NotesDao
import com.flux.data.model.NotesModel
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flux.data.model.Converter

@Database(entities = [NotesModel::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class NotesDatabase : RoomDatabase() {
    abstract val dao: NotesDao
}