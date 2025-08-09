package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoModel (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val workspaceId: Long = 0L,
    val title: String = "",
    val items: List<TodoItem> = emptyList()
)

data class TodoItem(
    var value: String="",
    var isChecked: Boolean=false
)