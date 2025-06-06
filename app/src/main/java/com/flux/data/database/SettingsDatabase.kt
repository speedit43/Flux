package com.flux.data.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.flux.data.dao.SettingsDao
import com.flux.data.model.SettingsModel

@Database(entities = [SettingsModel::class], version = 1, exportSchema = false)
abstract class SettingsDatabase : RoomDatabase() {
    abstract val dao: SettingsDao
}