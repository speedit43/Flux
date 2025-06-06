package com.flux.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flux.data.dao.LabelDao
import com.flux.data.model.LabelModel
import com.flux.ui.events.LabelEvents
import com.flux.ui.state.LabelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LabelViewModel @Inject constructor(
    private val dao: LabelDao
) : ViewModel() {

    private val _state: MutableStateFlow<LabelState> = MutableStateFlow(LabelState())
    val state: StateFlow<LabelState> = _state.asStateFlow()

    init { loadAllLabels() }

    fun onEvent(event: LabelEvents) { viewModelScope.launch { reduce(event) } }

    private fun updateState(reducer: (LabelState) -> LabelState) { _state.value = reducer(_state.value) }
    private suspend fun reduce(event: LabelEvents) {
        when (event) {
            is LabelEvents.DeleteLabel -> deleteLabel(event.label)
            is LabelEvents.UpsertLabel -> upsertLabel(event.label)
        }
    }

    private fun loadAllLabels() {
        updateState { it.copy(isLoading = true) }
        viewModelScope.launch {
            dao.loadAllLabels().collect { data ->
                if (data.isEmpty()) {
                    seedDefaultLabels()
                } else {
                    updateState { it.copy(isLoading = false, data = data) }
                }
            }
        }
    }

    private suspend fun seedDefaultLabels() {
        withContext(Dispatchers.IO) {
            dao.upsertLabels(listOf(LabelModel(value = "Bookmark"), LabelModel(value = "Default")))
        }
    }

    private suspend fun upsertLabel(label: LabelModel) {
        withContext(Dispatchers.IO) {
            dao.upsertLabel(label)
        }
    }

    private suspend fun deleteLabel(label: LabelModel) {
        withContext(Dispatchers.IO) {
            dao.deleteLabel(label)
        }
    }
}
