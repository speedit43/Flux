package com.flux.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flux.data.model.EventModel
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun upsertEvent(task: EventModel)

    @Delete
    suspend fun deleteEvent(task: EventModel)

    @Query("Delete FROM EventModel where workspaceId = :workspaceId")
    suspend fun deleteAllWorkspaceEvents(workspaceId: Int)

    @Query("SELECT * FROM EventModel WHERE workspaceId = :workspaceId")
    fun loadAllEvents(workspaceId: Int): Flow<List<EventModel>>
}