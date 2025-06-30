package com.flux.ui.events

import com.flux.data.model.WorkspaceModel


sealed class WorkspaceEvents {
    data class DeleteSpace(val space: WorkspaceModel): WorkspaceEvents()
    data class UpsertSpace(val space: WorkspaceModel): WorkspaceEvents()
}