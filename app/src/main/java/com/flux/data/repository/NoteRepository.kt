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
    suspend fun deleteNotes(notes: List<Long>)
    suspend fun deleteAllWorkspaceNotes(workspaceId: Long)
    fun loadAllNotes(workspaceId: Long): Flow<List<NotesModel>>
    fun loadAllLabels(workspaceId: Long): Flow<List<LabelModel>>
}
