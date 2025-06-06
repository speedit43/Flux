package com.flux.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flux.data.dao.LabelDao
import com.flux.data.model.Converter
import com.flux.data.model.LabelModel

@Database(entities = [LabelModel::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class LabelDatabase : RoomDatabase() {
    abstract val dao: LabelDao
}