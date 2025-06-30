package com.flux.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flux.data.model.HabitInstanceModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitInstanceDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun upsertInstance(habitInstance: HabitInstanceModel)

    @Query("DELETE FROM HabitInstanceModel WHERE habitId IN (:habitId)")
    suspend fun deleteAllInstances(habitId: Long)

    @Delete
    suspend fun deleteInstance(habitInstance: HabitInstanceModel)

    @Query("DELETE FROM HabitInstanceModel WHERE workspaceId = :workspaceId")
    suspend fun deleteAllWorkspaceInstance(workspaceId: Long)

    @Query("SELECT * FROM HabitInstanceModel where workspaceId in (:workspaceId)")
    fun loadAllInstances(workspaceId: Long): Flow<List<HabitInstanceModel>>
}