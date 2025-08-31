package com.flux.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import com.flux.data.repository.NoteRepository
import com.flux.ui.events.NotesEvents
import com.flux.ui.state.NotesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    val repository: NoteRepository
) : ViewModel() {

    private val mutex = Mutex()
    private val _state: MutableStateFlow<NotesState> = MutableStateFlow(NotesState())
    val state: StateFlow<NotesState> = _state.asStateFlow()

    fun onEvent(event: NotesEvents) {
        viewModelScope.launch { reduce(event = event) }
    }

    private suspend fun updateState(reducer: (NotesState) -> NotesState) {
        mutex.withLock { _state.value = reducer(_state.value) }
    }

    private suspend fun reduce(event: NotesEvents) {
        when (event) {
            is NotesEvents.UpsertNote -> updateNotes(event.data)
            is NotesEvents.DeleteNotes -> deleteNotes(event.data)
            is NotesEvents.TogglePinMultiple -> togglePinMultiple(event.data)
            is NotesEvents.DeleteNote -> deleteNote(event.data)
            is NotesEvents.DeleteLabel -> deleteLabel(event.data)
            is NotesEvents.UpsertLabel -> upsertLabel(event.data)
            is NotesEvents.LoadAllNotes -> loadAllNotes(event.workspaceId)
            is NotesEvents.LoadAllLabels -> loadAllLabels(event.workspaceId)
            is NotesEvents.DeleteAllWorkspaceNotes -> deleteWorkspaceNotes(event.workspaceId)
            is NotesEvents.ClearSelection -> {
                updateState { it.copy(selectedNotes = emptyList()) }
            }

            is NotesEvents.SelectNotes -> {
                updateState { it.copy(selectedNotes = it.selectedNotes.plus(event.noteId)) }
            }

            is NotesEvents.UnSelectNotes -> {
                updateState { it.copy(selectedNotes = it.selectedNotes.minus(event.noteId)) }
            }

            is NotesEvents.SelectAllNotes -> {
                updateState { it.copy(selectedNotes = it.allNotes.map { note -> note.notesId }) }
            }
        }
    }

    private fun deleteNotes(data: List<NotesModel>) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteNotes(data.map { it.notesId }) }
    }

    private fun deleteNote(data: NotesModel) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteNote(data) }
    }

    private fun deleteLabel(data: LabelModel) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteLabel(data) }
    }

    private fun upsertLabel(data: LabelModel) {
        viewModelScope.launch(Dispatchers.IO) { repository.upsertLabel(data) }
    }

    private fun deleteWorkspaceNotes(workspaceId: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteAllWorkspaceNotes(workspaceId) }
    }

    private fun togglePinMultiple(data: List<NotesModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            val isAllPinned = data.all { it.isPinned }
            if (isAllPinned) {
                val updatedNotes = data.map { it.copy(isPinned = false) }
                repository.upsertNotes(updatedNotes)
            } else {
                val updatedNotes = data.map { it.copy(isPinned = true) }
                repository.upsertNotes(updatedNotes)
            }
        }
    }

    private suspend fun loadAllNotes(workspaceId: String) {
        updateState { it.copy(isNotesLoading = true) }

        repository.loadAllNotes(workspaceId)
            .distinctUntilChanged()
            .collect { data ->
                val sortedData = data.sortedByDescending { it.lastEdited }
                updateState { it.copy(isNotesLoading = false, allNotes = sortedData) }
            }
    }

    private suspend fun loadAllLabels(workspaceId: String) {
        updateState { it.copy(isLabelsLoading = true) }
        repository.loadAllLabels(workspaceId)
            .collect { data -> updateState { it.copy(isLabelsLoading = false, allLabels = data) } }
    }

    private fun updateNotes(data: NotesModel) {
        val isNewNote = state.value.allNotes.none { it.notesId == data.notesId }
        val isBlankNote = data.title.trim()
            .isBlank() && data.description.trim() == "<br>" && data.labels.isEmpty()
        if (isNewNote && isBlankNote) return

        viewModelScope.launch(Dispatchers.IO) { repository.upsertNote(data) }
    }
}
