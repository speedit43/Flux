package com.flux.ui.state

import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel

data class NotesState(
    val isNotesLoading: Boolean = true,
    val isLabelsLoading: Boolean = true,
    val allNotes: List<NotesModel> = emptyList(),
    val allLabels: List<LabelModel> = emptyList()
)
