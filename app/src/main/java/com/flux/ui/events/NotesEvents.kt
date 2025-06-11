package com.flux.ui.events

import com.flux.data.model.NotesModel

sealed class NotesEvents {
    data class DeleteNote(val data: NotesModel): NotesEvents()
    data class DeleteNotes(val data: List<NotesModel>): NotesEvents()
    data class TogglePinMultiple(val data: List<NotesModel>): NotesEvents()
    data class UpsertNote(val data: NotesModel) : NotesEvents()
}