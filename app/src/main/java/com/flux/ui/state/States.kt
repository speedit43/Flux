package com.flux.ui.state

data class States (
    val notesState: NotesState,
    val labelState: LabelState,
    val workspaceState: WorkspaceState,
    val settings: Settings
)