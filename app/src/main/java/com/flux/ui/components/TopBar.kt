package com.flux.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.flux.data.model.WorkspaceModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsTopBar(
    canUndo: Boolean,
    canRedo: Boolean,
    isPinned: Boolean,
    onBackPressed: () -> Unit,
    onDelete: () -> Unit,
    onAddLabel: () -> Unit,
    onTogglePinned: () -> Unit,
    onAboutClicked: ()->Unit,
    onUndo: ()->Unit,
    onRedo: ()->Unit
){
    CenterAlignedTopAppBar(
        title = {
            Row {
                IconButton(onClick = { if(canUndo) onUndo() }) { Icon(Icons.AutoMirrored.Filled.Undo, null, modifier = Modifier.alpha(if(canUndo) 1f else 0.5f)) }
                IconButton(onClick = { if(canRedo) onRedo() }) { Icon(Icons.AutoMirrored.Filled.Redo, null, modifier = Modifier.alpha(if(canRedo) 1f else 0.5f)) }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        navigationIcon = { IconButton(onClick = onBackPressed) { Icon(Icons.AutoMirrored.Default.ArrowBack, null) }},
        actions = {
            IconButton(onClick = onBackPressed) { Icon(Icons.Outlined.Check, null) }
            DropdownMenuWithDetails(isPinned, onTogglePinned, onAddLabel, onAboutClicked, onDelete)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceSelectedBar(
    totalSelectedSpaces: Int,
    isAllSelected: Boolean,
    onSelectAll: ()->Unit,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        title = {},
        navigationIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, null) }
                Text(totalSelectedSpaces.toString(), style = MaterialTheme.typography.titleLarge)
            }
        },
        actions = {
            if(!isAllSelected) { IconButton(onClick = onSelectAll ) { Icon(Icons.Default.SelectAll, null) } }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceTopBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<WorkspaceModel>,
    onSettingsClicked: () -> Unit,
    onCloseClicked: () -> Unit,
) {

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        title = {},
        actions = {
            WorkspaceSearchBar(
                textFieldState,
                onSearch,
                searchResults,
                onSettingsClicked,
                onCloseClicked
            )
        }
    )
}

