package com.flux.ui.screens.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import com.flux.navigation.Loader
import com.flux.navigation.NavRoutes
import com.flux.ui.components.AddLabelDialog
import com.flux.ui.components.DeleteLabelDialog
import com.flux.ui.components.EmptyNotes
import com.flux.ui.components.NotesHomeComponent
import com.flux.ui.components.NotesScaffold
import com.flux.ui.events.LabelEvents
import com.flux.ui.events.NotesEvents
import com.flux.ui.events.SettingEvents
import com.flux.ui.state.Settings
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesHome(
    navController: NavController,
    settings: Settings,
    isLoading: Boolean,
    radius: Int,
    allNotes: List<NotesModel>,
    allLabels: List<LabelModel>,
    snackBarHostState: SnackbarHostState,
    onNotesEvents: (NotesEvents) -> Unit,
    onLabelEvents: (LabelEvents) -> Unit,
    onSettingEvents: (SettingEvents) ->Unit
) {
    val isGridView = settings.data.isGridView
    var query by rememberSaveable { mutableStateOf("") }
    var isAddLabelClicked by remember { mutableStateOf(false) }
    val selectedNotesIds = remember { mutableStateListOf<UUID>() }
    var selectedTab = rememberSaveable { mutableStateOf("Default") }
    val filteredNotes = allNotes.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    val pinnedNotes = if(selectedTab.value=="Bookmark") filteredNotes.filter { it.isBookmarked && it.isPinned } else filteredNotes.filter { it.isPinned && it.labels.contains(selectedTab.value) }
    val unPinnedNotes = if(selectedTab.value=="Bookmark") filteredNotes.filter { !it.isPinned && it.isBookmarked } else filteredNotes.filter { !it.isPinned && it.labels.contains(selectedTab.value) }
    var showDeleteLabel by remember { mutableStateOf(false) }
    var isEditLabelClicked by remember { mutableStateOf(false) }
    var longPressedLabel by remember { mutableStateOf("") }

    if(showDeleteLabel){
        DeleteLabelDialog(
            onConfirmation = {
                onNotesEvents(NotesEvents.DeleteLabel(longPressedLabel, pinnedNotes+unPinnedNotes ))
                onLabelEvents(LabelEvents.DeleteLabel(allLabels.first { it.value==longPressedLabel }))}
        ) {
            longPressedLabel=""
            showDeleteLabel = false
        }
    }

    if (isLoading) {
        Loader()
    } else {

        NotesScaffold(
            navController=navController,
            query=query,
            showSearchBar = selectedNotesIds.isEmpty() && longPressedLabel.isBlank(),
            showSelectedNotesBar = longPressedLabel.isBlank(),
            showAddNote = longPressedLabel.isBlank(),
            isGridView=isGridView,
            selectedNoteIds=selectedNotesIds,
            allNotes=allNotes,
            filteredNotes=pinnedNotes+unPinnedNotes,
            onSearch = { query = it },
            onSearchBarCloseClicked = { query = "" },
            onSelectBarCloseClicked = { selectedNotesIds.clear() },
            onLabelBarCloseClicked = { longPressedLabel = "" },
            onTogglePin = {
                val selected = allNotes.filter { selectedNotesIds.contains(it.notesId) }
                onNotesEvents(NotesEvents.TogglePinMultiple(selected))
                selectedNotesIds.clear()
            },
            onToggleBookmark = {
                val selected = allNotes.filter { selectedNotesIds.contains(it.notesId) }
                onNotesEvents(NotesEvents.ToggleBookMarkMultiple(selected))
                selectedNotesIds.clear()
            },
            onDelete = {
                val selected = allNotes.filter { selectedNotesIds.contains(it.notesId) }
                onNotesEvents(NotesEvents.DeleteMultiple(selected))
                selectedNotesIds.clear()
                query = ""
            },
            onDeselectAll = { selectedNotesIds.clear() },
            onSelectAll = {
                selectedNotesIds.clear()
                allNotes.forEach { selectedNotesIds.add(it.notesId) }
            },
            onDeleteLabel = { showDeleteLabel=true },
            onEditLabel = { isEditLabelClicked =true },
            onGridViewChange = { onSettingEvents(SettingEvents.UpdateSettings(settings.data.copy(isGridView = !isGridView))) }

        ) { innerPadding->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column {
                    LabelsList(
                        selectedLabel = selectedTab.value,
                        labels = allLabels,
                        onLabelClicked = { selectedTab.value = it },
                        onAddLabel = { isAddLabelClicked = true },
                        onLongPressed = { longPressedLabel=it }
                    )

                    if (pinnedNotes.isEmpty() && unPinnedNotes.isEmpty()) {
                        EmptyNotes()
                    } else {
                        NotesHomeComponent(
                            pinnedNotes,
                            unPinnedNotes,
                            radius,
                            isGridView,
                            selectedNotesIds,
                            onClick = { navController.navigate(NavRoutes.NoteDetails.withArgs(it)) },
                            onLongPressed = {
                                if (selectedNotesIds.contains(it.notesId)) selectedNotesIds.remove(it.notesId)
                                else selectedNotesIds.add(it.notesId)
                            }
                        )
                    }
                }
            }
        }
    }

    if (isAddLabelClicked || isEditLabelClicked) {
        val initialLabel = allLabels.firstOrNull { it.value == longPressedLabel } ?: LabelModel(value = "")

        AddLabelDialog(
            initialValue = if(isAddLabelClicked) "" else longPressedLabel,
            onConfirmation = {
                if (isEditLabelClicked){
                    selectedTab.value=it
                    onNotesEvents(NotesEvents.UpdateLabel(longPressedLabel, it, pinnedNotes+unPinnedNotes))
                    longPressedLabel=""
                }
                onLabelEvents(LabelEvents.UpsertLabel(initialLabel.copy(value = it))) },
            onDismissRequest = {
                longPressedLabel = ""
                isEditLabelClicked = false
                isAddLabelClicked = false
            }
        )
    }
}


@Composable
fun LabelsList(
    selectedLabel: String,
    labels: List<LabelModel>,
    onLabelClicked: (String) -> Unit,
    onAddLabel: () -> Unit,
    onLongPressed: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(labels) { label ->
            Card(
                modifier = Modifier
                    .height(36.dp)
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(50))
                    .combinedClickable(
                        onClick = {
                            onLongPressed("")
                            onLabelClicked(label.value) },
                        onLongClick = { if(label.value!="Bookmark") {
                            onLabelClicked(label.value)
                            onLongPressed(label.value)
                        } }
                    )
                ,
                shape = RoundedCornerShape(50),
                border = BorderStroke(
                    1.dp,
                    if (selectedLabel == label.value)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedLabel == label.value)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (label.value == "Bookmark") {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Text(
                            text = label.value,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selectedLabel == label.value)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        item {
            Card(
                onClick = onAddLabel,
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .height(36.dp)
                    .wrapContentWidth(),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NewLabel,
                        contentDescription = "Add label",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

