package com.flux.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun DropdownMenuWithDetails(
    isPinned: Boolean,
    isBookmarked: Boolean,
    onTogglePinned: ()->Unit,
    onToggleBookmark: ()->Unit,
    onAddLabel: ()->Unit,
    onDelete: ()->Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // First section
            DropdownMenuItem(
                text = {
                    val text=if (isPinned) "Unpin" else "Pin"
                    Text(text) },
                leadingIcon = { val icon = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin
                    Icon(icon, contentDescription = null) },
                onClick = onTogglePinned
            )
            DropdownMenuItem(
                text = {
                    val text=if (isBookmarked) "Bookmarked" else "Bookmark"
                    Text(text) },
                leadingIcon = { val icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder
                    Icon(icon, contentDescription = null) },
                onClick = onToggleBookmark
            )

            HorizontalDivider()

            // Second section
            DropdownMenuItem(
                text = { Text("Add Label") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Label, contentDescription = null) },
                trailingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = onAddLabel
            )

            HorizontalDivider()

            DropdownMenuItem(
                text = { Text("Delete Note") },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                onClick = onDelete
            )

        }
    }
}