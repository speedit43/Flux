package com.flux.ui.events

import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import java.time.LocalDate

sealed class TaskEvents {
    data class DeleteAllWorkspaceEvents(val workspaceId: Int): TaskEvents()
    data class LoadAllInstances(val workspaceId: Int): TaskEvents()
    data class LoadAllTask(val workspaceId: Int): TaskEvents()
    data class LoadTodayTask(val workspaceId: Int): TaskEvents()
    data class LoadDateTask(val workspaceId: Int, val selectedDate: LocalDate): TaskEvents()
    data class UpsertTask(val taskEvent: EventModel): TaskEvents()
    data class DeleteTask(val taskEvent: EventModel): TaskEvents()
    data class ToggleStatus(val taskInstance: EventInstanceModel): TaskEvents()
}