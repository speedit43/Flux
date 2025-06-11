package com.flux.ui.screens.notes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import com.flux.navigation.NavRoutes
import com.flux.other.EditAction
import com.flux.ui.components.ActionType
import com.flux.ui.components.NoteDetailsTopBar
import com.flux.ui.components.NotesInputCard
import com.flux.ui.components.SelectLabelDialog
import com.flux.ui.components.SettingOption
import com.flux.ui.components.shapeManager
import com.flux.ui.events.NotesEvents
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetails(
    navController: NavController,
    workspaceId: Int,
    note: NotesModel,
    allLabels: List<LabelModel>,
    snackBarHostState: SnackbarHostState,
    onNotesEvents: (NotesEvents) -> Unit
) {
    var isPinned by rememberSaveable(note.notesId) { mutableStateOf(note.isPinned) }
    val actionHistory = remember { mutableStateListOf<EditAction>() }
    val redoHistory = remember { mutableStateListOf<EditAction>() }

    var title by rememberSaveable { mutableStateOf(note.title) }
    var description by rememberSaveable { mutableStateOf(note.description) }

    val noteLabels = remember {
        mutableStateListOf<LabelModel>().apply {
            addAll(allLabels.filter { note.labels.contains(it.labelId) })
        }
    }

    var showSelectLabels by remember { mutableStateOf(false) }
    var showAboutNotes by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    NotesInfoBottomSheet(words = countWords("$title $description"), characters = countCharacters("$title $description"), lastEdited = formatLastEdited(note.lastEdited), isVisible = showAboutNotes, sheetState = sheetState, onDismiss = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showAboutNotes=false
            }
        }
    })

    if(showSelectLabels){
        SelectLabelDialog(noteLabels, allLabels, onConfirmation = {
            noteLabels.clear()
            noteLabels.addAll(it)},
            onDismissRequest = { showSelectLabels=false },
            onAddLabel = { navController.navigate(NavRoutes.EditLabels.withArgs(workspaceId)) }
        )
    }

    fun undo() {
        if (actionHistory.isNotEmpty()) {
            when (val last = actionHistory.removeAt(actionHistory.lastIndex)) {
                is EditAction.TitleChanged -> {
                    redoHistory.add(EditAction.TitleChanged(last.old, last.new))
                    title = last.old
                }
                is EditAction.DescriptionChanged -> {
                    redoHistory.add(EditAction.DescriptionChanged(last.old, last.new))
                    description = last.old
                }
            }
        }
    }

    fun redo() {
        if (redoHistory.isNotEmpty()) {
            when (val next = redoHistory.removeAt(redoHistory.lastIndex)) {
                is EditAction.TitleChanged -> {
                    title = next.new
                    actionHistory.add(EditAction.TitleChanged(next.old, next.new))
                }
                is EditAction.DescriptionChanged -> {
                    description = next.new
                    actionHistory.add(EditAction.DescriptionChanged(next.old, next.new))
                }
            }
        }
    }

    BackHandler {
        onNotesEvents(NotesEvents.UpsertNote(note.copy(title=title, description = description, isPinned = isPinned, labels = noteLabels.map { it.labelId }.toList())))
        navController.popBackStack()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            NoteDetailsTopBar(
                actionHistory.isNotEmpty(),
                redoHistory.isNotEmpty(),
                isPinned,
                onBackPressed = {
                    onNotesEvents(NotesEvents.UpsertNote(note.copy(title=title, description = description, isPinned = isPinned, lastEdited = Date(), labels = noteLabels.map { it.labelId }.toList())))
                    navController.popBackStack()
                },
                onTogglePinned = { isPinned = !isPinned },
                onDelete = {
                    onNotesEvents(NotesEvents.DeleteNote(note))
                    navController.popBackStack()
                },
                onAddLabel = { showSelectLabels=true },
                onAboutClicked = { showAboutNotes=true },
                onUndo = { undo() },
                onRedo = { redo()}
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        NotesInputCard(innerPadding, title, description, noteLabels, onTitleChange = { new->
            if (new != title) {
                actionHistory.add(EditAction.TitleChanged(title, new))
                title = new
                redoHistory.clear()
            }}, onDescriptionChange = { new->
            if (new != description) {
                actionHistory.add(EditAction.DescriptionChanged(description, new))
                description = new
                redoHistory.clear()
            }
        }, onLabelClicked = { showSelectLabels=true })
    }
}

fun formatLastEdited(date: Date): String {
    val zoneId = ZoneId.systemDefault()
    val localDateTime = date.toInstant().atZone(zoneId).toLocalDateTime()

    return if (localDateTime.toLocalDate() == LocalDate.now()) {
        localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } else {
        localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }
}

fun countWords(text: String): Int {
    return text.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
}

fun countCharacters(text: String, includeSpaces: Boolean = true): Int {
    return if (includeSpaces) text.length else text.count { !it.isWhitespace() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesInfoBottomSheet(
    words: Int,
    characters: Int,
    lastEdited: String,
    isVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit
){
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            LazyColumn (Modifier.fillMaxWidth().padding(16.dp)) {
                item {
                    SettingOption(
                        radius = shapeManager(isFirst = true, radius = 32),
                        icon = Icons.Default.Edit,
                        title = "Last Edited",
                        description = lastEdited,
                        actionType = ActionType.None
                    )
                }

                item {
                    SettingOption(
                        radius = shapeManager(radius = 32),
                        icon = Icons.Default.Numbers,
                        title = "Word Count",
                        description = words.toString(),
                        actionType = ActionType.None
                    )
                }

                item {
                    SettingOption(
                        radius = shapeManager(radius = 32, isLast = true),
                        icon = Icons.Default.Abc,
                        title = "Character Count",
                        description = characters.toString(),
                        actionType = ActionType.None
                    )
                }
            }
        }
    }
}
