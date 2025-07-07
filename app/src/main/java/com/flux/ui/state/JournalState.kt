package com.flux.ui.state

import com.flux.data.model.JournalModel
import java.time.LocalDate

data class JournalState(
    val isLoading: Boolean = false,
    val allEntries: List<JournalModel> = emptyList(),
    val isLoadingMore: Boolean = false,
    val currentLoadedMonth: LocalDate = LocalDate.now()
)