package com.flux.ui.events

import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel

sealed class NotesEvents {
    data class DeleteAllWorkspaceNotes(val workspaceId: String) : NotesEvents()
    data class LoadAllNotes(val workspaceId: String) : NotesEvents()
    data class LoadAllLabels(val workspaceId: String) : NotesEvents()
    data class DeleteNote(val data: NotesModel) : NotesEvents()
    data class DeleteNotes(val data: List<NotesModel>) : NotesEvents()
    data class TogglePinMultiple(val data: List<NotesModel>) : NotesEvents()
    data class UpsertNote(val data: NotesModel) : NotesEvents()
    data class DeleteLabel(val data: LabelModel) : NotesEvents()
    data class UpsertLabel(val data: LabelModel) : NotesEvents()
    data class SelectNotes(val noteId: String) : NotesEvents()
    data class UnSelectNotes(val noteId: String) : NotesEvents()
    data object ClearSelection : NotesEvents()
    data object SelectAllNotes : NotesEvents()
}