package com.flux.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsTopBar(
    isPinned: Boolean,
    isBookmarked: Boolean,
    onBackPressed: () -> Unit,
    onDelete: () -> Unit,
    onAddLabel: () -> Unit,
    onTogglePinned: () -> Unit,
    onToggleBookmark: () ->Unit
){
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        title = {
            TextButton(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Edit, null)
                    Text(
                        "Edit Note",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        navigationIcon = { IconButton(onClick = onBackPressed) { Icon(Icons.AutoMirrored.Default.ArrowBack, null) }},
        actions = { IconButton(onClick = onBackPressed) { Icon(Icons.Outlined.Check, null) }
            DropdownMenuWithDetails(isPinned, isBookmarked, onTogglePinned, onToggleBookmark, onAddLabel, onDelete) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesHomeSelectedBar(
    totalSelectedNotes: Int,
    isAllSelected: Boolean,
    isAllPinned: Boolean=false,
    isAllBookMarked: Boolean=false,
    onSelectAll: ()->Unit,
    onDeselectAll: ()->Unit,
    onPinClicked: ()->Unit,
    onBookMarkClicked: ()->Unit,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        title = {},
        navigationIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPinClicked) { val icon= if(isAllPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin
                    Icon(icon, null)  }
                IconButton(onClick = onBookMarkClicked) {
                    val icon=if(isAllBookMarked) Icons.Filled.Bookmarks else Icons.Outlined.Bookmarks
                    Icon(icon, null) }
                if(!isAllSelected){ IconButton(onClick = onSelectAll ) { Icon(Icons.Default.SelectAll, null) } }
                else { IconButton(onClick = onDeselectAll ) { Icon(Icons.Default.Deselect, null) } }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
            }
        },
        actions = {
            Text(totalSelectedNotes.toString(), style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, null) }
        }
    )
}

