package com.flux.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flux.data.dao.EventInstanceDao
import com.flux.data.model.Converter
import com.flux.data.model.EventInstanceModel

@Database(entities = [EventInstanceModel::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class EventInstanceDatabase: RoomDatabase() {
    abstract val dao: EventInstanceDao
}
