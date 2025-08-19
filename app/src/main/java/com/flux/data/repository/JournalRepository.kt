package com.flux.data.repository

import com.flux.data.model.JournalModel
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    suspend fun upsertEntry(event: JournalModel)
    suspend fun deleteEntry(event: JournalModel)
    suspend fun deleteAllWorkspaceEntry(workspaceId: Long)
    fun loadEntriesForMonth(
        workspaceId: Long,
        startOfMonth: Long,
        endOfMonth: Long
    ): Flow<List<JournalModel>>
}