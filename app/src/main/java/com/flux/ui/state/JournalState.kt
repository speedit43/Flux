package com.flux.ui.state

import com.flux.data.model.JournalModel

data class JournalState(
    val isLoading: Boolean = false,
    val allEntries: List<JournalModel> = emptyList()
)