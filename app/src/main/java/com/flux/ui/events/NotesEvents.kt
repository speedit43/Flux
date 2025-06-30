package com.flux.ui.events

import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel

sealed class NotesEvents {
    data class DeleteAllWorkspaceNotes(val workspaceId: Int): NotesEvents()
    data class LoadAllNotes(val workspaceId: Int): NotesEvents()
    data class LoadAllLabels(val workspaceId: Int): NotesEvents()
    data class DeleteNote(val data: NotesModel): NotesEvents()
    data class DeleteNotes(val data: List<NotesModel>): NotesEvents()
    data class TogglePinMultiple(val data: List<NotesModel>): NotesEvents()
    data class UpsertNote(val data: NotesModel) : NotesEvents()
    data class DeleteLabel(val data: LabelModel): NotesEvents()
    data class UpsertLabel(val data: LabelModel): NotesEvents()
}