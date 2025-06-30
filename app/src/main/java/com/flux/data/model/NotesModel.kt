package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class NotesModel(
    @PrimaryKey(autoGenerate = true)
    val notesId: Long = 0L,
    val title: String = "",
    val description: String = "",
    val isPinned: Boolean = false,
    val workspaceId: Long = 0L,
    val labels: List<Long> = emptyList(),
    val lastEdited: Date = Date()
)
