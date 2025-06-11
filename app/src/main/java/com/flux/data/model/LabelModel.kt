package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LabelModel(
    @PrimaryKey(autoGenerate = true)
    val labelId: Int = 0,
    val value: String="",
    val workspaceId: Int=0
)
