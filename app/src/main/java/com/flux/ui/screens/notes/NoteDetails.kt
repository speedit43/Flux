package com.flux.ui.screens.notes


import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import com.flux.ui.components.NoteDetailsTopBar
import com.flux.ui.components.NotesInputCard
import com.flux.ui.components.SelectLabelDialog
import com.flux.ui.events.NotesEvents
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetails(
    navController: NavController,
    allLabels: List<LabelModel>,
    allNotes: List<NotesModel>,
    noteId: UUID?,
    snackBarHostState: SnackbarHostState,
    onNotesEvents: (NotesEvents) -> Unit
) {

    val note = allNotes.find { it.notesId == noteId } ?: NotesModel()
    var showAddLabel by remember { mutableStateOf(false) }
    var isBookmarked by rememberSaveable(noteId) { mutableStateOf(note.isBookmarked) }
    var isPinned by rememberSaveable(noteId) { mutableStateOf(note.isPinned) }
    var title by rememberSaveable(noteId) { mutableStateOf(note.title) }
    var description by rememberSaveable(noteId) { mutableStateOf(note.description) }
    val noteLabels = remember(noteId) { mutableStateListOf<String>().apply { addAll(note.labels) } }

    if(showAddLabel){
        SelectLabelDialog(noteLabels, allLabels, onConfirmation = {
            noteLabels.clear()
            noteLabels.addAll(it)
        }) {
            showAddLabel=false
        }
    }

    BackHandler {
        onNotesEvents(NotesEvents.UpsertNote(note.copy(note.notesId, title, description, isPinned, isBookmarked, noteLabels)))
        navController.popBackStack()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            NoteDetailsTopBar(
                isPinned,
                isBookmarked,
                onBackPressed = {
                    onNotesEvents(NotesEvents.UpsertNote(note.copy(note.notesId, title, description, isPinned, isBookmarked, noteLabels)))
                    navController.popBackStack()
                },
                onTogglePinned = { isPinned = !isPinned },
                onToggleBookmark = { isBookmarked = !isBookmarked },
                onDelete = {
                    onNotesEvents(NotesEvents.DeleteNote(note))
                    navController.popBackStack()
                },
                onAddLabel = { showAddLabel=true }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        NotesInputCard(innerPadding, title, description, noteLabels, onTitleChange = {title=it}, onDescriptionChange = { description=it }, onLabelRemove = { noteLabels.remove(it) })
    }
}
