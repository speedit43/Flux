package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class JournalModel(
    @PrimaryKey
    val journalId: String = UUID.randomUUID().toString(),
    val workspaceId: String = "",
    val text: String = "",
    val dateTime: Long = System.currentTimeMillis(),
    val images: List<String> = emptyList()
)