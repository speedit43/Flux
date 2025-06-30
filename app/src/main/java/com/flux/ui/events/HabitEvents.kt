package com.flux.ui.events

import android.content.Context
import com.flux.data.model.HabitInstanceModel
import com.flux.data.model.HabitModel

sealed class HabitEvents {
    data class DeleteAllWorkspaceHabits(val workspaceId: Long): HabitEvents()
    data class LoadAllHabits(val workspaceId: Long): HabitEvents()
    data class LoadAllInstances(val workspaceId: Long): HabitEvents()
    data class DeleteHabit(val habit: HabitModel): HabitEvents()
    data class UpsertHabit(val context: Context, val habit: HabitModel, val adjustedTime: Long): HabitEvents()
    data class MarkDone(val habitInstance: HabitInstanceModel): HabitEvents()
    data class MarkUndone(val habitInstance: HabitInstanceModel): HabitEvents()
}