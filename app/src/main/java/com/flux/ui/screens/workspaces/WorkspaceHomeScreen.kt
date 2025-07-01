package com.flux.ui.screens.workspaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.flux.R
import com.flux.data.model.EventModel
import com.flux.data.model.HabitModel
import com.flux.data.model.WorkspaceModel
import com.flux.other.cancelReminder
import com.flux.ui.components.CircleWrapper
import com.flux.ui.components.DeleteAlert
import com.flux.ui.components.shapeManager
import com.flux.ui.events.HabitEvents
import com.flux.ui.events.NotesEvents
import com.flux.ui.events.TaskEvents
import com.flux.ui.events.TodoEvents
import com.flux.ui.events.WorkspaceEvents

@Composable
fun WorkspaceHomeScreen(
    radius: Int,
    workspace: WorkspaceModel,
    onAddSpaces: () -> Unit,
    allEvents: List<EventModel>,
    allHabits: List<HabitModel>,
    navigateToTab: (Int) -> Unit,
    onNotesEvents: (NotesEvents)->Unit,
    onTaskEvents: (TaskEvents)->Unit,
    onHabitEvents: (HabitEvents)-> Unit,
    onTodoEvents: (TodoEvents)->Unit,
    onWorkspaceEvents: (WorkspaceEvents) -> Unit,
) {
    val context= LocalContext.current
    val workspaceId=workspace.workspaceId
    var updatedWorkspace by remember { mutableStateOf(workspace) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var isNotesRemoved by remember { mutableStateOf(!workspace.isNotesAdded) }
    var isHabitRemoved by remember { mutableStateOf(!workspace.isHabitsAdded) }
    var isEventsRemoved by remember { mutableStateOf(!workspace.isEventsAdded) }
    var isTodoRemoved by remember { mutableStateOf(!workspace.isTodoAdded) }

    if(showRemoveDialog){
        DeleteAlert(
            icon = Icons.Default.RemoveCircle,
            dialogTitle = stringResource(R.string.removeDialogTitle),
            dialogText = stringResource(R.string.removeDialogText),
            onConfirmation = {
                if(isNotesRemoved){ onNotesEvents(NotesEvents.DeleteAllWorkspaceNotes(workspaceId)) }
                if(isTodoRemoved) { onTodoEvents(TodoEvents.DeleteAllWorkspaceLists(workspaceId)) }
                if(isEventsRemoved) {
                    allEvents.forEach { event-> cancelReminder(context, event.eventId, "EVENT", event.title, event.description, event.repetition.toString()) }
                    onTaskEvents(TaskEvents.DeleteAllWorkspaceEvents(workspaceId)) }
                if(isHabitRemoved) {
                    allHabits.forEach { habit-> cancelReminder(context, habit.habitId, "HABIT", habit.title, habit.description, "DAILY") }
                    onHabitEvents(HabitEvents.DeleteAllWorkspaceHabits(workspaceId)) }
                onWorkspaceEvents(WorkspaceEvents.UpsertSpace(updatedWorkspace))
                showRemoveDialog = false
            },
            onDismissRequest = { showRemoveDialog = false }
        )
    }

    ElevatedCard(
        shape = shapeManager(radius=radius*2),
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        onClick = {},
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)),
    ) {
        LazyColumn(
            Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (workspace.cover.isNotBlank()) {
                item{
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)) {
                        AsyncImage(
                            model = workspace.cover.toUri(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )

                        IconButton(
                            onClick = { onWorkspaceEvents(WorkspaceEvents.UpsertSpace(workspace.copy(cover = ""))) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White.copy(0.75f),
                                contentColor = MaterialTheme.colorScheme.error.copy(0.9f)
                            )
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete image")
                        }
                    }
                }
            }
            item {
                Column(Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = if (workspace.cover.isNotBlank()) 0.dp else 16.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Workspaces, null, modifier = Modifier.size(32.dp))
                        Text(
                            workspace.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Text(
                        workspace.description,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Light)
                    )
                }
            }
            if (workspace.isNotesAdded) {
                item {
                    SpacesCard(
                        radius=radius,
                        title = stringResource(R.string.Notes),
                        icon = Icons.AutoMirrored.Default.Notes,
                        onClick = { navigateToTab(R.string.Notes) },
                        onRemove = {
                            isNotesRemoved=true
                            updatedWorkspace=updatedWorkspace.copy(isNotesAdded = false)
                            showRemoveDialog=true
                        }
                    )
                }
            }
            if (workspace.isTodoAdded) {
                item {
                    SpacesCard(
                        radius=radius,
                        title = stringResource(R.string.To_Do),
                        icon = Icons.Default.Checklist,
                        onClick = { navigateToTab(R.string.To_Do) },
                        onRemove = {
                            isTodoRemoved=true
                            updatedWorkspace=workspace.copy(isTodoAdded = false)
                            showRemoveDialog=true
                        }
                    )
                }
            }
            if (workspace.isEventsAdded) {
                item {
                    SpacesCard(
                        radius=radius,
                        title = stringResource(R.string.Events),
                        icon = Icons.Default.Event,
                        onClick = { navigateToTab(R.string.Events) },
                        onRemove = {
                            isEventsRemoved=true
                            updatedWorkspace=updatedWorkspace.copy(isEventsAdded = false)
                            showRemoveDialog=true
                        }
                    )
                }
            }
            if (workspace.isCalenderAdded) {
                item {
                    SpacesCard(
                        radius=radius,
                        title = stringResource(R.string.Calender),
                        icon = Icons.Default.CalendarMonth,
                        onClick = { navigateToTab(R.string.Calender) },
                        onRemove = {
                            updatedWorkspace=updatedWorkspace.copy(isCalenderAdded = false)
                            showRemoveDialog=true
                        }
                    )
                }
            }
            if (workspace.isHabitsAdded) {
                item {
                    SpacesCard(
                        radius=radius,
                        title = stringResource(R.string.Habits),
                        icon = Icons.Default.EventAvailable,
                        onClick = { navigateToTab(R.string.Habits) },
                        onRemove = {
                            isHabitRemoved=true
                            updatedWorkspace=updatedWorkspace.copy(isHabitsAdded = false)
                            showRemoveDialog=true
                        }
                    )
                }
            }
            if (!workspace.isNotesAdded || !workspace.isTodoAdded || !workspace.isCalenderAdded || !workspace.isHabitsAdded || !workspace.isEventsAdded) {
                item {
                    SpacesCard(
                        radius=radius,
                        title = stringResource(R.string.Add_Space),
                        icon = Icons.Default.Add,
                        isAddNewSpace = true,
                        onClick = onAddSpaces,
                        onRemove = {}
                    )
                }
            }
        }
    }
}

@Composable
fun SpacesCard(
    radius: Int,
    title: String,
    icon: ImageVector,
    isAddNewSpace: Boolean=false,
    onRemove: ()->Unit,
    onClick: ()->Unit
){
    Card(
        shape = shapeManager(radius=radius*2),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircleWrapper(color = MaterialTheme.colorScheme.primary) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.onPrimary)
                }
                Text(text = title, fontWeight = FontWeight.Bold)
            }
            if (!isAddNewSpace){
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Remove, null, tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
