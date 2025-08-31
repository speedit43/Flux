package com.flux.data.repository

import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun upsertNote(note: NotesModel)
    suspend fun upsertLabel(label: LabelModel)
    suspend fun upsertNotes(notes: List<NotesModel>)
    suspend fun deleteNote(note: NotesModel)
    suspend fun deleteLabel(label: LabelModel)
    suspend fun deleteNotes(notes: List<String>)
    suspend fun deleteAllWorkspaceNotes(workspaceId: String)
    fun loadAllNotes(workspaceId: String): Flow<List<NotesModel>>
    fun loadAllLabels(workspaceId: String): Flow<List<LabelModel>>
}
