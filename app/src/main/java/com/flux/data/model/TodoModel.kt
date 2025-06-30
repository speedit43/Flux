package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val workspaceId: Int = 0,
    val title: String = "",
    val items: List<TodoItem> = emptyList()
)

data class TodoItem(
    var value: String="Title",
    var isChecked: Boolean=false
)