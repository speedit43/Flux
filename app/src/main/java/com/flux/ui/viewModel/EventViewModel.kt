package com.flux.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import com.flux.data.model.Repetition
import com.flux.data.repository.EventRepository
import com.flux.ui.events.TaskEvents
import com.flux.ui.state.EventState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor (
    val repository: EventRepository
) : ViewModel() {

    private val mutex = Mutex()
    private val _state: MutableStateFlow<EventState> = MutableStateFlow(EventState())
    val state: StateFlow<EventState> = _state.asStateFlow()

    fun onEvent(event: TaskEvents) { viewModelScope.launch { reduce(event = event) } }
    private suspend fun safeUpdateState(reducer: (EventState) -> EventState) { mutex.withLock { _state.value = reducer(_state.value) } }
    private suspend fun reduce(event: TaskEvents) {
        when (event) {
            is TaskEvents.DeleteTask -> deleteEvent(event.taskEvent)
            is TaskEvents.UpsertTask -> upsertEvent(event.taskEvent)
            is TaskEvents.ToggleStatus -> toggleStatus(event.taskInstance)
            is TaskEvents.LoadTodayTask -> loadTodayEvents(event.workspaceId)
            is TaskEvents.LoadDateTask -> loadDateEvents(event.workspaceId, event.selectedDate)
            is TaskEvents.LoadAllTask -> loadAllEvents(event.workspaceId)
            is TaskEvents.LoadAllInstances -> loadAllEventsInstances(event.workspaceId)
            is TaskEvents.DeleteAllWorkspaceEvents -> deleteWorkspaceEvents(event.workspaceId)
        }
    }

    private fun deleteEvent(data: EventModel) { viewModelScope.launch(Dispatchers.IO) { repository.deleteEvent(data) } }
    private fun upsertEvent(data: EventModel) { viewModelScope.launch(Dispatchers.IO) { repository.upsertEvent(data) } }
    private fun toggleStatus(data: EventInstanceModel) { viewModelScope.launch(Dispatchers.IO) { repository.toggleStatus(data) } }
    private fun deleteWorkspaceEvents(workspaceId: Int) { viewModelScope.launch(Dispatchers.IO) { repository.deleteAllWorkspaceEvent(workspaceId) } }
    private suspend fun loadTodayEvents(workspaceId: Int) {
        val date = LocalDate.now()
        safeUpdateState { it.copy(isTodayEventLoading = true) }

        viewModelScope.launch {
            repository.loadAllEvents(workspaceId)
                .distinctUntilChanged()
                .collect { data ->
                    val filteredData = data.filter { task ->
                        val taskStartDate = Instant.ofEpochMilli(task.startDateTime)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        when (task.repetition) {
                            Repetition.NONE -> taskStartDate == date
                            Repetition.DAILY -> !date.isBefore(taskStartDate)
                            Repetition.WEEKLY -> !date.isBefore(taskStartDate) &&
                                    date.dayOfWeek == taskStartDate.dayOfWeek
                            Repetition.MONTHLY -> !date.isBefore(taskStartDate) &&
                                    date.dayOfMonth == taskStartDate.dayOfMonth
                            Repetition.YEARLY -> !date.isBefore(taskStartDate) &&
                                    date.dayOfYear == taskStartDate.dayOfYear
                        }
                    }
                    safeUpdateState { it.copy(isTodayEventLoading = false, todayEvents = filteredData) }
                }
        }
    }

    private suspend fun loadDateEvents(workspaceId: Int, date: LocalDate) {
        safeUpdateState { it.copy(isDatedEventLoading = true) }
        viewModelScope.launch {
            repository.loadAllEvents(workspaceId)
                .distinctUntilChanged()
                .collect { data ->
                    val filteredData = data.filter { task ->
                        val taskStartDate = Instant.ofEpochMilli(task.startDateTime)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        when (task.repetition) {
                            Repetition.NONE -> taskStartDate == date
                            Repetition.DAILY -> !date.isBefore(taskStartDate)
                            Repetition.WEEKLY -> !date.isBefore(taskStartDate) &&
                                    date.dayOfWeek == taskStartDate.dayOfWeek
                            Repetition.MONTHLY -> !date.isBefore(taskStartDate) &&
                                    date.dayOfMonth == taskStartDate.dayOfMonth
                            Repetition.YEARLY -> !date.isBefore(taskStartDate) &&
                                    date.dayOfYear == taskStartDate.dayOfYear
                        }
                    }
                    safeUpdateState { it.copy(isDatedEventLoading = false, datedEvents = filteredData) }
                }
        }
    }

    private fun loadAllEvents(workspaceId: Int) {
        viewModelScope.launch {
            repository.loadAllEvents(workspaceId)
                .distinctUntilChanged()
                .collect { data ->
                    val sortedData = data.sortedBy { it.startDateTime }
                    safeUpdateState { it.copy(allEvent = sortedData) }
                }
        }
    }

    private suspend fun loadAllEventsInstances(workspaceId: Int) {
        safeUpdateState { it.copy(isTodayEventLoading = true, isDatedEventLoading = true) }
        viewModelScope.launch {
            repository.loadAllEventInstances(workspaceId)
                .distinctUntilChanged()
                .collect { data ->
                    safeUpdateState {
                        it.copy(
                            isTodayEventLoading = false,
                            isDatedEventLoading = false,
                            allEventInstances = data
                        )
                    }
                }
        }
    }
}
