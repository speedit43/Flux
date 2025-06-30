package com.flux.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flux.data.model.NotesModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun upsertNote(notes: NotesModel)

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun upsertNotes(notes: List<NotesModel>)

    @Delete
    suspend fun deleteNote(note: NotesModel)

    @Query("DELETE FROM NotesModel WHERE notesId IN (:ids)")
    suspend fun deleteNotes(ids: List<Int>)

    @Query("DELETE FROM NotesModel WHERE workspaceId = :workspaceId")
    suspend fun deleteAllWorkspaceNotes(workspaceId: Int)

    @Query("SELECT * FROM NotesModel where workspaceId IN (:workspaceId)")
    fun loadAllNotes(workspaceId: Int): Flow<List<NotesModel>>
}
