package com.flux.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flux.data.dao.WorkspaceDao
import com.flux.data.model.WorkspaceModel

@Database(entities = [WorkspaceModel::class], version = 2, exportSchema = false)
abstract class WorkspaceDatabase : RoomDatabase() {
    abstract val dao: WorkspaceDao
}
