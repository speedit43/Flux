package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class TodoModel(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val workspaceId: String ="",
    val title: String = "",
    val items: List<TodoItem> = emptyList()
)

data class TodoItem(
    var value: String = "",
    var isChecked: Boolean = false
)