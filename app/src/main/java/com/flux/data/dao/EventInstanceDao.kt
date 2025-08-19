package com.flux.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flux.data.model.EventInstanceModel
import kotlinx.coroutines.flow.Flow

@Dao
interface EventInstanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEventInstance(event: EventInstanceModel)

    @Query("DELETE FROM EventInstancemodel WHERE eventId IN (:eventId)")
    suspend fun deleteAllEventInstance(eventId: Long)

    @Query("DELETE FROM EventInstanceModel WHERE workspaceId = :workspaceId")
    suspend fun deleteAllWorkspaceInstance(workspaceId: Long)

    @Query("SELECT * FROM EventInstanceModel where workspaceId in (:workspaceId)")
    fun loadAllInstances(workspaceId: Long): Flow<List<EventInstanceModel>>
}