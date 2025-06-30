package com.flux.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flux.data.model.HabitInstanceModel
import com.flux.data.model.HabitModel
import com.flux.data.repository.HabitRepository
import com.flux.ui.events.HabitEvents
import com.flux.ui.state.HabitState
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
class HabitViewModel @Inject constructor(private val repository: HabitRepository) : ViewModel()
{
    private val _state: MutableStateFlow<HabitState> = MutableStateFlow(HabitState())
    val state: StateFlow<HabitState> = _state.asStateFlow()
    private val mutex = Mutex()
    fun onEvent(event: HabitEvents) { viewModelScope.launch { reduce(event = event) } }
    private suspend fun updateState(reducer: (HabitState) -> HabitState) { mutex.withLock { _state.value = reducer(_state.value) } }
    private suspend fun reduce(event: HabitEvents) {
        when (event) {
            is HabitEvents.DeleteHabit -> deleteHabit(event.habit)
            is HabitEvents.LoadAllHabits -> loadAllHabits(event.workspaceId)
            is HabitEvents.UpsertHabit -> upsertHabit(event.habit)
            is HabitEvents.LoadAllInstances -> loadAllInstances(event.workspaceId)
            is HabitEvents.MarkDone -> upsertInstance(event.habitInstance)
            is HabitEvents.MarkUndone -> deleteInstance(event.habitInstance)
            is HabitEvents.DeleteAllWorkspaceHabits -> deleteWorkspaceHabits(event.workspaceId)
        }
    }
    private fun deleteInstance(instance: HabitInstanceModel){ viewModelScope.launch(Dispatchers.IO) { repository.deleteInstance(instance) } }
    private fun upsertInstance(instance: HabitInstanceModel){ viewModelScope.launch(Dispatchers.IO) { repository.upsertHabitInstance(instance) }}
    private fun deleteHabit(data: HabitModel) { viewModelScope.launch(Dispatchers.IO) { repository.deleteHabit(data) } }
    private fun upsertHabit(data: HabitModel) { viewModelScope.launch(Dispatchers.IO) { repository.upsertHabit(data) } }
    private fun deleteWorkspaceHabits(workspaceId: Int){ viewModelScope.launch(Dispatchers.IO) { repository.deleteAllWorkspaceHabit(workspaceId) } }
    private suspend fun loadAllInstances(workspaceId: Int){
        updateState { it.copy(isLoading = true) }
        viewModelScope.launch { repository.loadAllHabitInstance(workspaceId).distinctUntilChanged().collect { data-> updateState { it.copy(isLoading = false, allInstances = data) } } }
    }
    private suspend fun loadAllHabits(workspaceId: Int) {
        updateState { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.loadAllHabits(workspaceId)
                .distinctUntilChanged()
                .collect { data ->
                    val sortedData = data.sortedBy { it.startDateTime }
                    updateState { it.copy(isLoading = false, allHabits = sortedData) }
                }
        }
    }
}