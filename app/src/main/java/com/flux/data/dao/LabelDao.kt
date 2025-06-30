package com.flux.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flux.data.model.LabelModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun upsertLabel(label: LabelModel)

    @Delete
    suspend fun deleteLabel(label: LabelModel)

    @Query("DELETE FROM LabelModel WHERE workspaceId = :workspaceId")
    suspend fun deleteAllWorkspaceLabels(workspaceId: Long)

    @Query("SELECT * FROM LabelModel where workspaceId IN (:workspaceId)")
    fun loadAllLabels(workspaceId: Long): Flow<List<LabelModel>>
}
