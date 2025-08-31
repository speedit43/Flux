package com.flux.data.repository

import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun upsertEvent(event: EventModel)
    suspend fun deleteEvent(event: EventModel)
    suspend fun deleteAllWorkspaceEvent(workspaceId: String)
    suspend fun toggleStatus(eventInstance: EventInstanceModel)
    suspend fun loadAllEvents(): List<EventModel>
    fun loadAllWorkspaceEvents(workspaceId: String): Flow<List<EventModel>>
    fun loadAllEventInstances(workspaceId: String): Flow<List<EventInstanceModel>>
}