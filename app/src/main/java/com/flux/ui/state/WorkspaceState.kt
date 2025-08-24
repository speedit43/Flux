package com.flux.ui.state

import com.flux.data.model.WorkspaceModel

data class WorkspaceState(
    val isLoading: Boolean = true,
    val allSpaces: List<WorkspaceModel> = emptyList()
)
