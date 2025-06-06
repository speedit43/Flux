package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class NotesModel(
    @PrimaryKey
    val notesId: UUID = UUID.randomUUID(),
    val title: String = "",
    val description: String = "",
    val isPinned: Boolean = false,
    val isBookmarked: Boolean = false,
    val labels: List<String> = listOf<String>("Default")
)
