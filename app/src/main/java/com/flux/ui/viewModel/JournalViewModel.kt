package com.flux.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flux.data.model.JournalModel
import com.flux.data.repository.JournalRepository
import com.flux.ui.events.JournalEvents
import com.flux.ui.state.JournalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    val repository: JournalRepository
) : ViewModel() {
    private val _state: MutableStateFlow<JournalState> = MutableStateFlow(JournalState())
    val state: StateFlow<JournalState> = _state.asStateFlow()

    fun onEvent(event: JournalEvents) { viewModelScope.launch { reduce(event = event) } }
    private fun updateState(reducer: (JournalState) -> JournalState) { _state.value = reducer(_state.value) }
    private fun reduce(event: JournalEvents) {
        when (event) {
            is JournalEvents.DeleteEntry -> deleteEntry(event.entry)
            is JournalEvents.DeleteWorkspaceEntries -> deleteAllWorkspaceEntries(event.workspaceId)
            is JournalEvents.LoadJournalEntries -> { loadJournalEntries(event.workspaceId) }
            is JournalEvents.UpsertEntry -> upsertEntry(event.entry)
        }
    }

    private fun deleteAllWorkspaceEntries(workspaceId: Long) { viewModelScope.launch(Dispatchers.IO) { repository.deleteAllWorkspaceEntry(workspaceId) } }
    private fun deleteEntry(data: JournalModel) { viewModelScope.launch(Dispatchers.IO) { repository.deleteEntry(data) } }
    private fun upsertEntry(data: JournalModel) { viewModelScope.launch(Dispatchers.IO) { repository.upsertEntry(data) } }

    fun loadJournalEntries(workspaceId: Long) {
        updateState { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            repository.loadAllEntries(workspaceId)
                .distinctUntilChanged()
                .collect { allEntries ->
                    updateState { oldState ->
                        oldState.copy(
                            isLoading = false,
                            allEntries = allEntries
                        )
                    }
                }
        }
    }
}