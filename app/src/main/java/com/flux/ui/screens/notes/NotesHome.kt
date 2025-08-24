package com.flux.ui.screens.notes

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import com.flux.navigation.Loader
import com.flux.navigation.NavRoutes
import com.flux.ui.components.EmptyNotes
import com.flux.ui.components.NotesPreviewCard
import com.flux.ui.events.NotesEvents

@OptIn(ExperimentalMaterial3Api::class)
fun LazyListScope.notesHomeItems(
    navController: NavController,
    workspaceId: Long,
    selectedNotes: List<Long>,
    query: String,
    radius: Int,
    allLabels: List<LabelModel>,
    isLoading: Boolean,
    allNotes: List<NotesModel>,
    onNotesEvents: (NotesEvents) -> Unit
) {
    val pinnedNotes = allNotes.filter { it.isPinned }
    val unPinnedNotes = allNotes.filter { !it.isPinned }

    when {
        isLoading -> item { Loader() }
        else ->
            if (pinnedNotes.isEmpty() && unPinnedNotes.isEmpty()) {
                item { EmptyNotes() }
            } else {
                if (pinnedNotes.isNotEmpty()) {
                    item {
                        Text(
                            "Pinned",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                items(pinnedNotes.filter {
                    it.title.lowercase().contains(query.lowercase()) || it.description.lowercase()
                        .contains(query.lowercase())
                }) { note ->
                    NotesPreviewCard(
                        radius = radius,
                        isSelected = selectedNotes.contains(note.notesId),
                        note = note,
                        labels = allLabels.filter { note.labels.contains(it.labelId) }
                            .map { it.value },
                        onClick = {
                            navController.navigate(
                                NavRoutes.NoteDetails.withArgs(
                                    workspaceId,
                                    it
                                )
                            )
                        },
                        onLongPressed = {
                            if (selectedNotes.contains(note.notesId)) {
                                onNotesEvents(NotesEvents.UnSelectNotes(note.notesId))
                            } else {
                                onNotesEvents(NotesEvents.SelectNotes(note.notesId))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                }
                if (pinnedNotes.isNotEmpty()) {
                    item {
                        Text(
                            "Others",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                items(unPinnedNotes.filter {
                    it.title.lowercase().contains(query.lowercase()) || it.description.lowercase()
                        .contains(query.lowercase())
                }) { note ->
                    NotesPreviewCard(
                        radius = radius,
                        isSelected = selectedNotes.contains(note.notesId),
                        note = note,
                        labels = allLabels.filter { note.labels.contains(it.labelId) }
                            .map { it.value },
                        onClick = {
                            navController.navigate(
                                NavRoutes.NoteDetails.withArgs(
                                    workspaceId,
                                    it
                                )
                            )
                        },
                        onLongPressed = {
                            if (selectedNotes.contains(note.notesId)) {
                                onNotesEvents(NotesEvents.UnSelectNotes(note.notesId))
                            } else {
                                onNotesEvents(NotesEvents.SelectNotes(note.notesId))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
    }
}
