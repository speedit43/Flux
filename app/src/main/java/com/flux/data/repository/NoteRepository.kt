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
    suspend fun deleteNotes(notes: List<Int>)
    suspend fun deleteAllWorkspaceNotes(workspaceId: Int)
    fun loadAllNotes(workspaceId: Int): Flow<List<NotesModel>>
    fun loadAllLabels(workspaceId: Int): Flow<List<LabelModel>>
}
