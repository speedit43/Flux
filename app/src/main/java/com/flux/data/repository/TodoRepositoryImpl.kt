package com.flux.data.repository

import com.flux.data.dao.TodoDao
import com.flux.data.model.TodoModel
import com.flux.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    val dao: TodoDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : TodoRepository {
    override fun loadAllLists(workspaceId: Long): Flow<List<TodoModel>> {
        return dao.loadAllLists(workspaceId)
    }

    override suspend fun upsertList(list: TodoModel) {
        return withContext(ioDispatcher) { dao.upsertList(list) }
    }

    override suspend fun deleteList(list: TodoModel) {
        return withContext(ioDispatcher) { dao.deleteList(list) }
    }

    override suspend fun deleteAllWorkspaceLists(workspaceId: Long) {
        return withContext(ioDispatcher) { dao.deleteAllWorkspaceLists(workspaceId) }
    }
}