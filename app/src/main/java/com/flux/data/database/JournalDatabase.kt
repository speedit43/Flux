package com.flux.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flux.data.dao.JournalDao
import com.flux.data.model.Converter
import com.flux.data.model.JournalModel


@Database(entities = [JournalModel::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class JournalDatabase : RoomDatabase() {
    abstract val dao: JournalDao
}
