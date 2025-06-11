package com.flux.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import com.flux.data.model.WorkspaceModel
import com.flux.ui.events.SettingEvents
import com.flux.ui.state.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<WorkspaceModel>,
    onSettingsClicked: () -> Unit,
    onCloseClicked: () -> Unit,
){
    val query = textFieldState.text.toString()
    var expanded by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxWidth().semantics { isTraversalGroup = true }) {
        SearchBar(
            modifier = Modifier.align(Alignment.TopCenter).wrapContentHeight().semantics { traversalIndex = 0f },
            inputField = {
                WorkspaceSearchInputField(query, expanded,
                    {
                        textFieldState.edit { replace(0, length, it) }
                        onSearch(it)
                        if (it.isBlank()) { expanded = false }
                    }, {
                        keyboardController?.hide()
                        onSearch(query)
                    }, {
                        textFieldState.edit { replace(0, length, "") }
                        onCloseClicked()
                        onSearch("")
                        expanded = false
                    },
                    { expanded = it },
                    onSettingsClicked
                )
            },
            colors = SearchBarDefaults.colors(containerColor = if (expanded) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)),
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            LazyColumn {
                items(searchResults.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }) { data ->
                    WorkSpacesCard(
                        icon = Icons.Default.Work,
                        workspace = data,
                        onClick = {},
                        onLongPress = { })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceSearchInputField(
    query: String,
    expanded: Boolean,
    onQueryChange: (String)->Unit,
    onSearch: (String)->Unit,
    onSearchClosed: ()-> Unit,
    onExpandedChange: (Boolean)->Unit,
    onSettingsClicked: ()->Unit
){
    SearchBarDefaults.InputField(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        placeholder = { Text("Search Workspaces...") },
        leadingIcon = { Icon(Icons.Rounded.Search, "Search") },
        trailingIcon = {
            Row { if (query.isNotBlank()) { CloseButton(onSearchClosed) }
            if(!expanded) {SettingsButton( onSettingsClicked )} }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesSearchBar(
    settings: Settings,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<NotesModel>,
    allLabels: List<LabelModel>,
    onCloseClicked: () -> Unit,
    onSettingsEvents: (SettingEvents)->Unit,
    selectedNotes: List<NotesModel>,
    onNotesClick: (Int)->Unit,
    onNotesLongPress: (NotesModel)->Unit
){
    val query = textFieldState.text.toString()
    var expanded by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val radius=settings.data.cornerRadius
    val isGridView=settings.data.isGridView
    val filteredResults=searchResults.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    val pinnedNotes=filteredResults.filter { it.isPinned }
    val unPinnedNotes=filteredResults.filter { !it.isPinned }

    Box(modifier = Modifier.fillMaxWidth().semantics { isTraversalGroup = true }) {
        SearchBar(
            modifier = Modifier.align(Alignment.TopCenter).wrapContentHeight().semantics { traversalIndex = 0f },
            inputField = {
                NotesSearchInputField(isGridView, query, expanded,
                    {
                        textFieldState.edit { replace(0, length, it) }
                        onSearch(it)
                        if (it.isBlank()) { expanded = false }
                    }, {
                        keyboardController?.hide()
                        onSearch(query)
                    }, {
                        textFieldState.edit { replace(0, length, "") }
                        onCloseClicked()
                        onSearch("")
                        expanded = false
                    },
                    { expanded = it },
                    { onSettingsEvents(SettingEvents.UpdateSettings(settings.data.copy(isGridView=!isGridView))) }
                )
            },
            colors = SearchBarDefaults.colors(containerColor = if (expanded) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)),
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            if (pinnedNotes.isEmpty() && unPinnedNotes.isEmpty()){ EmptyNotes() }
            else{ NotesPreviewGrid(radius, isGridView, allLabels, pinnedNotes, unPinnedNotes, selectedNotes, onNotesClick, onNotesLongPress) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesSearchInputField(
    isGridView: Boolean,
    query: String,
    expanded: Boolean,
    onQueryChange: (String)->Unit,
    onSearch: (String)->Unit,
    onSearchClosed: ()-> Unit,
    onExpandedChange: (Boolean)->Unit,
    onGridViewChange: ()->Unit
){
    SearchBarDefaults.InputField(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        placeholder = { Text("Search Notes...") },
        leadingIcon = { Icon(Icons.Rounded.Search, "Search") },
        trailingIcon = { Row {
            if (query.isNotBlank()) { CloseButton(onSearchClosed) }
            GridViewButton(isGridView, onGridViewChange)
        } }
    )
}
