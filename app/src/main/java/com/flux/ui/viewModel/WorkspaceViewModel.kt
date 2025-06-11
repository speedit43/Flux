package com.flux.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flux.data.dao.WorkspaceDao
import com.flux.data.model.WorkspaceModel
import com.flux.ui.events.WorkspaceEvents
import com.flux.ui.state.WorkspaceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor (
    val dao: WorkspaceDao
) : ViewModel() {

    private val _state: MutableStateFlow<WorkspaceState> = MutableStateFlow(WorkspaceState())
    val state: StateFlow<WorkspaceState> = _state.asStateFlow()

    init { loadWorkspace()  }

    fun onEvent(event: WorkspaceEvents) { viewModelScope.launch { reduce(event = event) } }
    private fun updateState(reducer: (WorkspaceState) -> WorkspaceState) { _state.value = reducer(_state.value) }

    private fun reduce(event: WorkspaceEvents) {
        when (event) {
            is WorkspaceEvents.DeleteSpaces -> deleteWorkspace(event.spacesId)
            is WorkspaceEvents.UpsertSpace -> upsertWorkspace(event.space)
        }
    }

    private fun loadWorkspace(){
        updateState { it.copy(isLoading = true) }

        viewModelScope.launch {
            dao.loadAllWorkspaces().collect { data->
                updateState { it.copy(isLoading = false, allSpaces = data) }
            }
        }
    }

    private fun upsertWorkspace(data : WorkspaceModel){
        viewModelScope.launch(Dispatchers.IO) {
            dao.upsertWorkspace(data)
        }
    }

    private fun deleteWorkspace(spacesId: List<Int>){
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteWorkspacesByIds(spacesId)
        }
    }
}