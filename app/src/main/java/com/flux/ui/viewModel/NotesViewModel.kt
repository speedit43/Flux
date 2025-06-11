package com.flux.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flux.data.dao.NotesDao
import com.flux.data.model.NotesModel
import com.flux.ui.events.NotesEvents
import com.flux.ui.state.NotesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor (
    val dao: NotesDao
) : ViewModel() {

    private val _state: MutableStateFlow<NotesState> = MutableStateFlow(NotesState())
    val state: StateFlow<NotesState> = _state.asStateFlow()

    init { loadAllNotes()  }

    fun onEvent(event: NotesEvents) { viewModelScope.launch { reduce(event = event) } }
    private fun updateState(reducer: (NotesState) -> NotesState) { _state.value = reducer(_state.value) }
    private fun reduce(event: NotesEvents) {
        when (event) {
            is NotesEvents.UpsertNote -> { updateNotes(event.data) }
            is NotesEvents.DeleteNotes -> { deleteNotes(event.data) }
            is NotesEvents.TogglePinMultiple -> { togglePinMultiple(event.data) }
            is NotesEvents.DeleteNote -> { deleteNote(event.data) }
        }
    }

    private fun togglePinMultiple(data: List<NotesModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            val isAllPinned=data.all { it.isPinned }
            if(isAllPinned){
                val updatedNotes = data.map { it.copy(isPinned = false) }
                dao.upsertNotes(updatedNotes)
            }
            else{
                val updatedNotes = data.map { it.copy(isPinned = true) }
                dao.upsertNotes(updatedNotes)
            }
        }
    }

    private fun deleteNotes(data: List<NotesModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteNotes(data.map { it.notesId })
        }
    }

    private fun deleteNote(data: NotesModel) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteNote(data)
        }
    }

    private fun loadAllNotes() {
        updateState { it.copy(isLoading = true) }

        viewModelScope.launch {
            dao.loadAllNotes()
                .collect { data ->
                    updateState { it.copy(isLoading = false, allNotes = data) } }

        }
    }

    private fun updateNotes(data: NotesModel) {
        val isNewNote = state.value.allNotes.none { it.notesId == data.notesId }
        val isBlankNote = data.title.isBlank() && data.description.isBlank() && data.labels.isEmpty()

        if (isNewNote && isBlankNote) return
        println("ViewModel $data")
        viewModelScope.launch(Dispatchers.IO) {
            dao.upsertNote(data)
        }
    }
}
