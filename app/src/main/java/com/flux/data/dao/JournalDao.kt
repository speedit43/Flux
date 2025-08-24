package com.flux.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flux.data.model.JournalModel
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntry(entry: JournalModel)

    @Delete
    suspend fun deleteEntry(entry: JournalModel)

    @Query("Delete FROM JournalModel where workspaceId = :workspaceId")
    suspend fun deleteAllWorkspaceEntries(workspaceId: Long)

    @Query(
        """
        SELECT * FROM JournalModel 
        WHERE workspaceId = :workspaceId
        ORDER BY dateTime DESC
    """
    )
    fun loadEntriesForMonth(workspaceId: Long): Flow<List<JournalModel>>
}