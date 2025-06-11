package com.flux.ui.screens.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import com.flux.navigation.Loader
import com.flux.navigation.NavRoutes
import com.flux.ui.components.EmptyNotes
import com.flux.ui.components.NotesPreviewGrid
import com.flux.ui.components.NotesSearchBar
import com.flux.ui.components.NotesSelectedBar
import com.flux.ui.events.NotesEvents
import com.flux.ui.events.SettingEvents
import com.flux.ui.state.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesHome(
    navController: NavController,
    workspaceId: Int,
    allLabels: List<LabelModel>,
    settings: Settings,
    isLoading: Boolean,
    allNotes: List<NotesModel>,
    onNotesEvents: (NotesEvents) -> Unit,
    onSettingEvents: (SettingEvents)->Unit
) {
    val selectedNotes = remember { mutableStateListOf<NotesModel>() }
    val pinnedNotes = allNotes.filter { it.isPinned }
    val unPinnedNotes = allNotes.filter { !it.isPinned }
    val isGridView = settings.data.isGridView
    val radius = settings.data.cornerRadius
    val areAllSelectedPinned = selectedNotes.all { it.isPinned }
    var query by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> Loader()
            else ->
                Column {
                    if(selectedNotes.isNotEmpty()){
                        NotesSelectedBar(
                            totalSelectedNotes = selectedNotes.size,
                            isAllSelected = selectedNotes.size == allNotes.size,
                            isAllPinned = areAllSelectedPinned,
                            onSelectAll = {
                                selectedNotes.clear()
                                allNotes.forEach { selectedNotes.add(it) }
                            },
                            onClose = { selectedNotes.clear() },
                            onPinClicked ={
                                onNotesEvents(NotesEvents.TogglePinMultiple(selectedNotes.toList()))
                                selectedNotes.clear()
                                          },
                            onDelete = {
                                onNotesEvents(NotesEvents.DeleteNotes(selectedNotes.toList()))
                                selectedNotes.clear()
                            }
                        )
                    }
                    else{
                        NotesSearchBar(
                            settings=settings,
                            textFieldState = TextFieldState(query),
                            onSearch = { query = it },
                            searchResults = allNotes,
                            allLabels=allLabels,
                            onCloseClicked = { query = "" },
                            onSettingsEvents = onSettingEvents,
                            selectedNotes=selectedNotes,
                            onNotesClick = { navController.navigate(NavRoutes.NoteDetails.withArgs(workspaceId, it)) },
                            onNotesLongPress = {
                                if (selectedNotes.contains(it)) selectedNotes.remove(it)
                                else selectedNotes.add(it)
                            }
                        )
                    }
                    if (pinnedNotes.isEmpty() && unPinnedNotes.isEmpty()){ EmptyNotes() }
                    else{
                        NotesPreviewGrid(
                            radius,
                            isGridView,
                            allLabels,
                            pinnedNotes,
                            unPinnedNotes,
                            selectedNotes,
                            onClick = { navController.navigate(NavRoutes.NoteDetails.withArgs(workspaceId, it)) },
                            onLongPressed = {
                                if (selectedNotes.contains(it)) selectedNotes.remove(it)
                                else selectedNotes.add(it)
                            }
                        )
                    }
                }
        }
    }
}
