package com.flux.ui.components


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.flux.data.model.NotesModel
import com.flux.navigation.BottomBar
import com.flux.navigation.NavRoutes
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicScaffold(
    title: String,
    onBackClicked: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScaffold(
    isGridView: Boolean,
    query: String,
    showSearchBar: Boolean,
    showSelectedNotesBar: Boolean,
    showAddNote: Boolean,
    allNotes: List<NotesModel>,
    filteredNotes: List<NotesModel>,
    navController: NavController,
    selectedNoteIds: List<UUID>,
    onSearchBarCloseClicked: ()->Unit,
    onSelectBarCloseClicked: ()->Unit,
    onLabelBarCloseClicked: ()->Unit,
    onSearch: (String)->Unit,
    onDelete: ()->Unit,
    onGridViewChange: ()->Unit,
    onSelectAll: ()->Unit,
    onDeselectAll: ()->Unit,
    onTogglePin: ()->Unit,
    onToggleBookmark: ()->Unit,
    onDeleteLabel: ()->Unit,
    onEditLabel: ()->Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            if (showSearchBar) {
                NotesSearchBar(
                    isGridView = isGridView,
                    textFieldState = TextFieldState(query),
                    onSearch = onSearch,
                    searchResults = filteredNotes,
                    onSettingsClicked = { navController.navigate(NavRoutes.Settings.route) },
                    onCloseClicked = onSearchBarCloseClicked,
                    onGridViewChange = onGridViewChange
                )
            } else if(showSelectedNotesBar) {
                val selectedNotes = allNotes.filter { selectedNoteIds.contains(it.notesId) }
                val areAllSelectedPinned = selectedNotes.all { it.isPinned }
                val areAllSelectedBookmarked = selectedNotes.all { it.isBookmarked }

                NotesHomeSelectedBar(
                    totalSelectedNotes = selectedNoteIds.size,
                    isAllSelected = selectedNoteIds.size == allNotes.size,
                    isAllPinned = areAllSelectedPinned,
                    isAllBookMarked = areAllSelectedBookmarked,
                    onSelectAll = onSelectAll,
                    onClose = onSelectBarCloseClicked,
                    onDeselectAll = onDeselectAll,
                    onPinClicked =onTogglePin,
                    onBookMarkClicked = onToggleBookmark,
                    onDelete = onDelete
                )
            }
            else {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onLabelBarCloseClicked) {
                            Icon(Icons.Filled.Close, "Close Selected Label")
                        }
                    },
                    actions = {
                        IconButton(onClick = onDeleteLabel) {
                            Icon(Icons.Filled.Delete, "Delete Label")
                        }
                        IconButton(onClick = onEditLabel) {
                            Icon(Icons.Filled.Edit, "Edit Label")
                        }
                    }
                )
            }
        },
        bottomBar = { BottomBar(navController) },
        floatingActionButton = {
            if(showAddNote){
                FloatingActionButton(onClick = { navController.navigate(NavRoutes.NoteDetails.route) }) {
                    Icon(Icons.Filled.Add, "Add Note")
                }
            }
        },
        content = content
    )
}