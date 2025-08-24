package com.flux.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flux.data.dao.WorkspaceDao
import com.flux.data.model.Converter
import com.flux.data.model.WorkspaceModel

@Database(entities = [WorkspaceModel::class], version = 3, exportSchema = false)
@TypeConverters(Converter::class)
abstract class WorkspaceDatabase : RoomDatabase() {
    abstract val dao: WorkspaceDao
}
