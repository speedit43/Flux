package com.flux.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flux.data.model.TodoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun upsertList(list: TodoModel)

    @Delete
    suspend fun deleteList(list: TodoModel)

    @Query("DELETE FROM TodoModel WHERE workspaceId = :workspaceId")
    fun deleteAllWorkspaceLists(workspaceId: Int)

    @Query("SELECT * FROM TodoModel where workspaceId IN (:workspaceId)")
    fun loadAllLists(workspaceId: Int): Flow<List<TodoModel>>
}