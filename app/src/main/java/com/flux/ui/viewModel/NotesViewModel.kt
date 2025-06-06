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
import kotlinx.coroutines.flow.distinctUntilChanged
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
            is NotesEvents.DeleteNote -> { deleteNote(event.data) }
            is NotesEvents.UpsertNote -> { updateNotes(event.data) }
            is NotesEvents.ChangeCurrNote -> { updateState { it.copy(currNote = event.note) } }
            is NotesEvents.DeleteMultiple -> { deleteMultipleNotes(event.data) }
            is NotesEvents.ToggleBookMarkMultiple -> { toggleBookmarkMultiple(event.data) }
            is NotesEvents.TogglePinMultiple -> { togglePinMultiple(event.data) }
            is NotesEvents.DeleteLabel -> { deleteLabel(event.label, event.notes) }
            is NotesEvents.UpdateLabel -> { updateLabel(event.oldLabel, event.newLabel, event.notes) }
        }
    }

    private fun deleteLabel(label: String, notes: List<NotesModel>){
        viewModelScope.launch(Dispatchers.IO) {
            val updatedNotes = notes.map { note -> note.copy(labels = note.labels.filterNot { it == label }) }
            dao.upsertNotes(updatedNotes)
        }
    }

    private fun updateLabel(oldLabel: String, newLabel: String, notes: List<NotesModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedNotes = notes.map { note -> note.copy(labels = note.labels.map { if (it == oldLabel) newLabel else it }) }
            dao.upsertNotes(updatedNotes)
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

    private fun toggleBookmarkMultiple(data: List<NotesModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            val isAllBookMarked = data.all { it.isBookmarked }
            if(isAllBookMarked){
                val updatedNotes = data.map { it.copy(isBookmarked = false) }
                dao.upsertNotes(updatedNotes)
            }
            else{
                val updatedNotes = data.map { it.copy(isBookmarked = true) }
                dao.upsertNotes(updatedNotes)
            }
        }
    }

    private fun deleteMultipleNotes(data: List<NotesModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteNotesByIds(data.map { it.notesId })
        }
    }

    private fun deleteNote(data: NotesModel){
        val currentState = state.value
        if (!currentState.allNotes.contains(data)) return

        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteNote(data)
        }
    }

    private fun loadAllNotes() {
        updateState { it.copy(isLoading = true) }

        viewModelScope.launch {
            dao.loadAllNotes()
                .distinctUntilChanged()
                .collect { data ->
                    updateState { it.copy(isLoading = false, allNotes = data) }
                }

        }
    }

    private fun updateNotes(data: NotesModel) {
        val isNewNote = state.value.allNotes.none { it.notesId == data.notesId }
        val isBlankNote = data.title.isBlank() && data.description.isBlank()

        if (isNewNote && isBlankNote) return

        viewModelScope.launch(Dispatchers.IO) {
            dao.upsertNote(data)
        }
    }
}
