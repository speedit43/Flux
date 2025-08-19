package com.flux.data.repository


import com.flux.data.dao.JournalDao
import com.flux.data.model.JournalModel
import com.flux.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class JournalRepositoryImpl @Inject constructor(
    private val dao: JournalDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : JournalRepository {
    override suspend fun upsertEntry(entry: JournalModel) {
        return withContext(ioDispatcher) { dao.upsertEntry(entry) }
    }

    override suspend fun deleteEntry(entry: JournalModel) {
        return withContext(ioDispatcher) { dao.deleteEntry(entry) }
    }

    override suspend fun deleteAllWorkspaceEntry(workspaceId: Long) {
        return withContext(ioDispatcher) { dao.deleteAllWorkspaceEntries(workspaceId) }
    }

    override fun loadEntriesForMonth(
        workspaceId: Long,
        startOfMonth: Long,
        endOfMonth: Long
    ): Flow<List<JournalModel>> {
        return dao.loadEntriesForMonth(workspaceId, startOfMonth, endOfMonth)
    }
}