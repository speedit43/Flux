package com.flux.ui.events

import com.flux.data.model.JournalModel

sealed class JournalEvents {
    data class UpsertEntry(val entry: JournalModel) : JournalEvents()
    data class DeleteEntry(val entry: JournalModel) : JournalEvents()
    data class DeleteWorkspaceEntries(val workspaceId: String) : JournalEvents()
    data class LoadJournalEntries(val workspaceId: String) : JournalEvents()
}