package com.flux.ui.events

import android.content.Context
import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import java.time.LocalDate

sealed class TaskEvents {
    data class DeleteAllWorkspaceEvents(val workspaceId: Long) : TaskEvents()
    data class LoadAllInstances(val workspaceId: Long) : TaskEvents()
    data class LoadAllTask(val workspaceId: Long) : TaskEvents()
    data class LoadTodayTask(val workspaceId: Long) : TaskEvents()
    data class LoadDateTask(val workspaceId: Long, val selectedDate: LocalDate) : TaskEvents()
    data class UpsertTask(
        val context: Context,
        val taskEvent: EventModel,
        val adjustedTime: Long?
    ) : TaskEvents()

    data class DeleteTask(val taskEvent: EventModel) : TaskEvents()
    data class ToggleStatus(val taskInstance: EventInstanceModel) : TaskEvents()
}