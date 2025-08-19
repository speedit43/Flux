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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    val repository: JournalRepository
) : ViewModel() {
    private val _state: MutableStateFlow<JournalState> = MutableStateFlow(JournalState())
    val state: StateFlow<JournalState> = _state.asStateFlow()

    fun onEvent(event: JournalEvents) {
        viewModelScope.launch { reduce(event = event) }
    }

    private fun updateState(reducer: (JournalState) -> JournalState) {
        _state.value = reducer(_state.value)
    }

    private fun reduce(event: JournalEvents) {
        when (event) {
            is JournalEvents.DeleteEntry -> deleteEntry(event.entry)
            is JournalEvents.DeleteWorkspaceEntries -> deleteAllWorkspaceEntries(event.workspaceId)
            is JournalEvents.LoadInitialEntries -> {
                loadInitialEntries(event.workspaceId)
            }

            is JournalEvents.UpsertEntry -> upsertEntry(event.entry)
            is JournalEvents.LoadPreviousMonthEntries -> {
                loadPreviousMonth(event.workspaceId)
            }
        }
    }

    private fun deleteAllWorkspaceEntries(workspaceId: Long) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteAllWorkspaceEntry(workspaceId) }
    }

    private fun deleteEntry(data: JournalModel) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteEntry(data) }
    }

    private fun upsertEntry(data: JournalModel) {
        viewModelScope.launch(Dispatchers.IO) { repository.upsertEntry(data) }
    }

    fun loadInitialEntries(workspaceId: Long) {
        updateState { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val currentMonth = LocalDate.now()
            loadMonth(workspaceId, currentMonth)
        }
    }

    fun loadPreviousMonth(workspaceId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = _state.value
            if (currentState.isLoadingMore) return@launch

            // Get the next previous month to load
            val nextMonth = currentState.currentLoadedMonth.minusMonths(1)

            updateState { it.copy(isLoadingMore = true, currentLoadedMonth = nextMonth) }
            loadMonth(workspaceId, nextMonth)
        }
    }

    private suspend fun loadMonth(workspaceId: Long, monthDate: LocalDate) {
        try {
            val zoneId = ZoneId.systemDefault()

            val startOfMonth = monthDate.withDayOfMonth(1)
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli()

            val endOfMonth = monthDate.withDayOfMonth(monthDate.lengthOfMonth())
                .atTime(LocalTime.MAX)
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli()

            repository.loadEntriesForMonth(workspaceId, startOfMonth, endOfMonth)
                .distinctUntilChanged()
                .collect { newEntries ->
                    updateState { oldState ->
                        val filteredEntries = oldState.allEntries.filterNot {
                            val entryDate = Instant.ofEpochMilli(it.dateTime)
                                .atZone(zoneId)
                                .toLocalDate()
                            entryDate.month == monthDate.month && entryDate.year == monthDate.year
                        }

                        oldState.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            allEntries = (filteredEntries + newEntries)
                                .distinctBy { it.journalId }
                                .sortedByDescending { it.dateTime }
                        )
                    }
                }
        } catch (_: Exception) {
            updateState { it.copy(isLoading = false, isLoadingMore = false) }
        }
    }

}