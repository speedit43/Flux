package com.flux.data.repository

import com.flux.data.model.HabitInstanceModel
import com.flux.data.model.HabitModel
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    suspend fun upsertHabit(habit: HabitModel)
    suspend fun deleteHabit(habit: HabitModel)
    suspend fun deleteAllWorkspaceHabit(workspaceId: String)
    suspend fun deleteInstance(habitInstance: HabitInstanceModel)
    suspend fun upsertHabitInstance(habitInstance: HabitInstanceModel)
    suspend fun loadAllHabits(): List<HabitModel>
    fun loadAllHabitsOfWorkspace(workspaceId: String): Flow<List<HabitModel>>
    fun loadAllHabitInstance(workspaceId: String): Flow<List<HabitInstanceModel>>
}