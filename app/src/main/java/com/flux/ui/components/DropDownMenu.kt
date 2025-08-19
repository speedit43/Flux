package com.flux.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.PhotoSizeSelectActual
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Workspaces
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flux.R

@Composable
fun DropdownMenuWithDetails(
    isPinned: Boolean,
    onTogglePinned: () -> Unit,
    onAddLabel: () -> Unit,
    onAboutClicked: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(if (isPinned) stringResource(R.string.Unpin) else stringResource(R.string.Pin)) },
                leadingIcon = {
                    Icon(
                        if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = null
                    )
                },
                onClick = onTogglePinned
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.Labels)) },
                leadingIcon = {
                    Icon(
                        Icons.AutoMirrored.Outlined.Label,
                        contentDescription = null
                    )
                },
                trailingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = {
                    expanded = false
                    onAddLabel()
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.About)) },
                leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                onClick = {
                    expanded = false
                    onAboutClicked()
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.error,
                    leadingIconColor = MaterialTheme.colorScheme.error
                ),
                text = { Text(stringResource(R.string.Delete_Note)) },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}

@Composable
fun WorkspaceMore(
    isLocked: Boolean,
    showEditLabel: Boolean,
    isPinned: Boolean,
    onEditDetails: () -> Unit,
    onEditLabel: () -> Unit,
    onEditIcon: () -> Unit,
    onAddCover: () -> Unit,
    onDelete: () -> Unit,
    onTogglePinned: () -> Unit,
    onToggleLock: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.Edit_Details)) },
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                onClick = {
                    expanded = false
                    onEditDetails()
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(if (isPinned) stringResource(R.string.Unpin) else stringResource(R.string.Pin)) },
                leadingIcon = {
                    Icon(
                        if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = null
                    )
                },
                onClick = {
                    expanded = false
                    onTogglePinned()
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.Change_Icon)) },
                leadingIcon = { Icon(Icons.Outlined.Workspaces, contentDescription = null) },
                onClick = {
                    expanded = false
                    onEditIcon()
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.Change_Cover)) },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.PhotoSizeSelectActual,
                        contentDescription = null
                    )
                },
                onClick = {
                    expanded = false
                    onAddCover()
                }
            )

            if (showEditLabel) {
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.Labels)) },
                    leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Outlined.Label,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        expanded = false
                        onEditLabel()
                    }
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = {
                    Text(
                        if (isLocked) stringResource(R.string.Unlock_Workspace) else stringResource(
                            R.string.Lock_Workspace
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        if (isLocked) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
                        contentDescription = null
                    )
                },
                onClick = {
                    expanded = false
                    onToggleLock()
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                colors = MenuDefaults.itemColors(
                    leadingIconColor = MaterialTheme.colorScheme.error,
                    textColor = MaterialTheme.colorScheme.error
                ),
                text = { Text(stringResource(R.string.Delete_Workspace)) },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}

@Composable
fun WorkspacePreviewMore(
    isPinned: Boolean,
    onDelete: () -> Unit,
    onTogglePinned: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.MoreHoriz,
                contentDescription = "More options"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(if (isPinned) stringResource(R.string.Unpin) else stringResource(R.string.Pin)) },
                leadingIcon = {
                    Icon(
                        if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = null
                    )
                },
                onClick = {
                    expanded = false
                    onTogglePinned()
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                colors = MenuDefaults.itemColors(
                    leadingIconColor = MaterialTheme.colorScheme.error,
                    textColor = MaterialTheme.colorScheme.error
                ),
                text = { Text(stringResource(R.string.Delete_Workspace)) },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}