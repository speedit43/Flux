package com.flux.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flux.data.model.NotesModel
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface NotesDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun upsertNote(note: NotesModel)

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun upsertNotes(notes: List<NotesModel>)

    @Delete
    suspend fun deleteNote(note: NotesModel)

    @Query("DELETE FROM NotesModel WHERE notesId IN (:ids)")
    suspend fun deleteNotesByIds(ids: List<UUID>)

    @Query("SELECT * FROM NotesModel")
    fun loadAllNotes(): Flow<List<NotesModel>>
}
