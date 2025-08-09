package com.flux.data.repository

import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun upsertEvent(event: EventModel): Long
    suspend fun deleteEvent(event: EventModel)
    suspend fun deleteAllWorkspaceEvent(workspaceId: Long)
    suspend fun toggleStatus(eventInstance: EventInstanceModel)
    suspend fun loadAllEvents(): List<EventModel>
    fun loadAllWorkspaceEvents(workspaceId: Long): Flow<List<EventModel>>
    fun loadAllEventInstances(workspaceId: Long): Flow<List<EventInstanceModel>>
}