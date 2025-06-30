package com.flux.data.repository

import com.flux.data.dao.LabelDao
import com.flux.data.dao.NotesDao
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import com.flux.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val labelDao: LabelDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): NoteRepository {
    override suspend fun upsertNote(note: NotesModel) { return withContext(ioDispatcher) { notesDao.upsertNote(note) } }
    override suspend fun upsertLabel(label: LabelModel) { return withContext(ioDispatcher) { labelDao.upsertLabel(label) } }
    override suspend fun upsertNotes(notes: List<NotesModel>) { return withContext(ioDispatcher) { notesDao.upsertNotes(notes) } }
    override suspend fun deleteNote(note: NotesModel) { return withContext(ioDispatcher) { notesDao.deleteNote(note) } }
    override suspend fun deleteLabel(label: LabelModel) { return withContext(ioDispatcher) { labelDao.deleteLabel(label) } }
    override suspend fun deleteNotes(notes: List<Long>) { return withContext(ioDispatcher) { notesDao.deleteNotes(notes) } }
    override fun loadAllNotes(workspaceId: Long): Flow<List<NotesModel>> { return notesDao.loadAllNotes(workspaceId) }
    override fun loadAllLabels(workspaceId: Long): Flow<List<LabelModel>> { return labelDao.loadAllLabels(workspaceId) }
    override suspend fun deleteAllWorkspaceNotes(workspaceId: Long) { return(withContext(ioDispatcher) {
        labelDao.deleteAllWorkspaceLabels(workspaceId)
        notesDao.deleteAllWorkspaceNotes(workspaceId)
    })}
}