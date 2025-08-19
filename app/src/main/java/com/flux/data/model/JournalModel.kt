package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class JournalModel(
    @PrimaryKey(autoGenerate = true)
    val journalId: Long = 0L,
    val workspaceId: Long = 0L,
    val text: String = "",
    val dateTime: Long = System.currentTimeMillis(),
    val images: List<String> = emptyList()
)