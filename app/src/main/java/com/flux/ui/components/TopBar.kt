package com.flux.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.flux.data.model.WorkspaceModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsTopBar(
    canUndo: Boolean,
    canRedo: Boolean,
    isPinned: Boolean,
    onBackPressed: () -> Unit,
    onDone: () -> Unit,
    onDelete: () -> Unit,
    onAddLabel: () -> Unit,
    onTogglePinned: () -> Unit,
    onAboutClicked: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Row {
                IconButton(onClick = { if (canUndo) onUndo() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.Undo,
                        null,
                        modifier = Modifier.alpha(if (canUndo) 1f else 0.5f)
                    )
                }
                IconButton(onClick = { if (canRedo) onRedo() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.Redo,
                        null,
                        modifier = Modifier.alpha(if (canRedo) 1f else 0.5f)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    null
                )
            }
        },
        actions = {
            IconButton(onClick = {
                onDone()
                onBackPressed()
            }) { Icon(Icons.Outlined.Check, null) }
            DropdownMenuWithDetails(isPinned, onTogglePinned, onAddLabel, onAboutClicked, onDelete)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceTopBar(
    workspace: WorkspaceModel,
    onBackPressed: () -> Unit,
    onDelete: () -> Unit,
    onTogglePinned: () -> Unit,
    onToggleLock: () -> Unit,
    onAddCover: () -> Unit,
    onEditDetails: () -> Unit,
    onEditLabel: () -> Unit,
    onRemoveCover: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (workspace.cover.isNotBlank()) 160.dp else 80.dp)
    ) {
        // Background image
        AsyncImage(
            model = workspace.cover.toUri(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Overlay TopAppBar
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            title = {},
            navigationIcon = {
                IconButton(
                    onClick = onBackPressed,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) { Icon(Icons.AutoMirrored.Default.ArrowBack, null) }
            },
            actions = {
                WorkspaceMore(
                    isCoverAdded = workspace.cover.isNotBlank(),
                    isLocked = workspace.passKey.isNotBlank(),
                    isPinned = workspace.isPinned,
                    showEditLabel = workspace.selectedSpaces.contains(1),
                    onDelete = onDelete,
                    onEditDetails = onEditDetails,
                    onEditLabel = onEditLabel,
                    onRemoveCover = onRemoveCover,
                    onAddCover = onAddCover,
                    onTogglePinned = onTogglePinned,
                    onToggleLock = onToggleLock
                )
            },
            modifier = Modifier.matchParentSize()
        )
    }
}
