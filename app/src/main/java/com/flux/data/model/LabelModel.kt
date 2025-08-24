package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LabelModel(
    @PrimaryKey(autoGenerate = true)
    val labelId: Long = 0L,
    val value: String = "",
    val workspaceId: Long = 0L
)
