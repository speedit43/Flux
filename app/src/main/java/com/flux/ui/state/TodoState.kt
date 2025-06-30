package com.flux.ui.state

import com.flux.data.model.TodoModel

data class TodoState(
    val isLoading: Boolean=false,
    val allLists: List<TodoModel> = emptyList()
)