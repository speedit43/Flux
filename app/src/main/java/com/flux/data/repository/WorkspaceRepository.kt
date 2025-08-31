package com.flux.data.repository

import com.flux.data.model.WorkspaceModel
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository {
    suspend fun upsertWorkspace(workspace: WorkspaceModel)
    suspend fun deleteWorkspace(workspace: WorkspaceModel)
    suspend fun upsertWorkspaces(spaces: List<WorkspaceModel>)
    fun loadAllWorkspaces(): Flow<List<WorkspaceModel>>
}