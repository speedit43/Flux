package com.flux.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flux.data.dao.EventDao
import com.flux.data.model.Converter
import com.flux.data.model.EventModel

@Database(entities = [EventModel::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class EventDatabase : RoomDatabase() {
    abstract val dao: EventDao
}
