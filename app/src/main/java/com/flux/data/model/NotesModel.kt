package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class NotesModel(
    @PrimaryKey(autoGenerate = true)
    val notesId: Int = 0,
    val title: String = "",
    val description: String = "",
    val isPinned: Boolean = false,
    val workspaceId: Int=0,
    val labels: List<Int> = emptyList(),
    val lastEdited: Date = Date()
)
