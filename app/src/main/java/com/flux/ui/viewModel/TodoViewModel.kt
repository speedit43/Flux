package com.flux.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flux.data.model.TodoModel
import com.flux.data.repository.TodoRepository
import com.flux.ui.events.TodoEvents
import com.flux.ui.state.TodoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    private val _state: MutableStateFlow<TodoState> = MutableStateFlow(TodoState())
    val state: StateFlow<TodoState> = _state.asStateFlow()

    fun onEvent(event: TodoEvents) {
        viewModelScope.launch { reduce(event = event) }
    }

    private fun updateState(reducer: (TodoState) -> TodoState) {
        _state.value = reducer(_state.value)
    }

    private suspend fun reduce(event: TodoEvents) {
        when (event) {
            is TodoEvents.DeleteList -> {
                deleteList(event.data)
            }

            is TodoEvents.LoadAllLists -> {
                loadAllLists(event.workspaceId)
            }

            is TodoEvents.UpsertList -> {
                upsertList(event.data)
            }

            is TodoEvents.DeleteAllWorkspaceLists -> deleteWorkspaceLists(event.workspaceId)
        }
    }

    private fun deleteWorkspaceLists(workspaceId: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteAllWorkspaceLists(workspaceId) }
    }

    private fun deleteList(data: TodoModel) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteList(data) }
    }

    private fun upsertList(data: TodoModel) {
        viewModelScope.launch(Dispatchers.IO) { repository.upsertList(data) }
    }

    private suspend fun loadAllLists(workspaceId: String) {
        updateState { it.copy(isLoading = true) }
        repository.loadAllLists(workspaceId).distinctUntilChanged()
            .collect { data -> updateState { it.copy(isLoading = false, allLists = data) } }
    }
}