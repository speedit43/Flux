package com.flux.ui.events

import com.flux.data.model.HabitInstanceModel
import com.flux.data.model.HabitModel

sealed class HabitEvents {
    data class DeleteAllWorkspaceHabits(val workspaceId: Int): HabitEvents()
    data class LoadAllHabits(val workspaceId: Int): HabitEvents()
    data class LoadAllInstances(val workspaceId: Int): HabitEvents()
    data class DeleteHabit(val habit: HabitModel): HabitEvents()
    data class UpsertHabit(val habit: HabitModel): HabitEvents()
    data class MarkDone(val habitInstance: HabitInstanceModel): HabitEvents()
    data class MarkUndone(val habitInstance: HabitInstanceModel): HabitEvents()
}