package com.flux.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkspaceModel(
    @PrimaryKey(autoGenerate = true)
    val workspaceId: Long = 0L,
    val title: String = "",
    val description: String = "",
    val colorInd: Int = 0,
    val cover: String = "",
    val icon: Int = 48,
    val passKey: String = "",
    val isPinned: Boolean = false,
    val selectedSpaces: List<Int> = emptyList()
)

data class Space(
    val id: Int,
    val title: String,
    val icon: ImageVector
)

val SpacesList = listOf<Space>(
    Space(1, "Notes", Icons.AutoMirrored.Default.Notes),
    Space(2, "To-Do", Icons.Default.TaskAlt),
    Space(3, "Events", Icons.Default.Event),
    Space(4, "Calendar", Icons.Default.CalendarMonth),
    Space(5, "Journal", Icons.Default.AutoStories),
    Space(6, "Habits", Icons.Default.EventAvailable),
    Space(7, "Analytics", Icons.Default.Analytics)
)