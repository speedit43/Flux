package com.flux.ui.events

import com.flux.data.model.WorkspaceModel


sealed class WorkspaceEvents {
    data class DeleteSpaces(val spacesId: List<Int>): WorkspaceEvents()
    data class UpsertSpace(val space: WorkspaceModel): WorkspaceEvents()
}