package com.flux.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flux.data.model.WorkspaceModel
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun upsertWorkspace(space: WorkspaceModel)

    @Query("DELETE FROM WorkspaceModel WHERE workspaceId IN (:workspaceIds)")
    suspend fun deleteWorkspacesByIds(workspaceIds: List<Int>)

    @Query("SELECT * FROM WorkspaceModel")
    fun loadAllWorkspaces(): Flow<List<WorkspaceModel>>
}