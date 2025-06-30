package com.flux.data.repository

import com.flux.data.dao.EventDao
import com.flux.data.dao.EventInstanceDao
import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import com.flux.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val eventInstanceDao: EventInstanceDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): EventRepository {
    override suspend fun upsertEvent(event: EventModel): Long { return withContext(ioDispatcher) { eventDao.upsertEvent(event) } }
    override suspend fun toggleStatus(eventInstance: EventInstanceModel) { return withContext(ioDispatcher) { eventInstanceDao.upsertEventInstance(eventInstance) } }
    override fun loadAllEvents(workspaceId: Long): Flow<List<EventModel>> { return eventDao.loadAllEvents(workspaceId) }
    override fun loadAllEventInstances(workspaceId: Long): Flow<List<EventInstanceModel>> { return eventInstanceDao.loadAllInstances(workspaceId) }
    override suspend fun deleteEvent(event: EventModel) { return withContext(ioDispatcher) {
        eventInstanceDao.deleteAllEventInstance(event.eventId)
        eventDao.deleteEvent(event) }
    }
    override suspend fun deleteAllWorkspaceEvent(workspaceId: Long) {
        return withContext(ioDispatcher) {
            eventDao.deleteAllWorkspaceEvents(workspaceId)
            eventInstanceDao.deleteAllWorkspaceInstance(workspaceId)
        }
    }
}