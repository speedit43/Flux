package com.flux.ui.events

import com.flux.data.model.NotesModel

sealed class NotesEvents {
    data class ChangeCurrNote(val note: NotesModel) : NotesEvents()
    data class DeleteNote(val data: NotesModel): NotesEvents()
    data class DeleteMultiple(val data: List<NotesModel>): NotesEvents()
    data class TogglePinMultiple(val data: List<NotesModel>): NotesEvents()
    data class ToggleBookMarkMultiple(val data: List<NotesModel>): NotesEvents()
    data class UpsertNote(val data: NotesModel) : NotesEvents()
    data class UpdateLabel(val oldLabel: String, val newLabel: String, val notes: List<NotesModel>): NotesEvents()
    data class DeleteLabel(val label: String, val notes: List<NotesModel>): NotesEvents()

}