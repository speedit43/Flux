package com.flux.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["value"], unique = true)])
data class LabelModel(
    @PrimaryKey(autoGenerate = true)
    val labelId: Int? = null,
    val value: String
)
