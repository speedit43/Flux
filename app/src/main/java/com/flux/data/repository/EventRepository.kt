package com.flux.data.repository

import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun upsertEvent(event: EventModel)
    suspend fun deleteEvent(event: EventModel)
    suspend fun deleteAllWorkspaceEvent(workspaceId: Int)
    suspend fun toggleStatus(eventInstance: EventInstanceModel)
    fun loadAllEvents(workspaceId: Int): Flow<List<EventModel>>
    fun loadAllEventInstances(workspaceId: Int): Flow<List<EventInstanceModel>>
}