package com.flux.ui.screens.workspaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import com.flux.data.model.WorkspaceModel
import com.flux.navigation.NavRoutes
import com.flux.ui.components.AddSpacesDialog
import com.flux.ui.components.CircleWrapper
import com.flux.ui.components.WorkspaceMore
import com.flux.ui.events.NotesEvents
import com.flux.ui.events.SettingEvents
import com.flux.ui.events.WorkspaceEvents
import com.flux.ui.screens.notes.NotesHome
import com.flux.ui.screens.tasks.TaskHome
import com.flux.ui.state.Settings

data class WorkspaceTab(
    val title: String,
    val icon: ImageVector,
    val content: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceDetails(
    navController: NavController,
    allLabels: List<LabelModel>,
    settings: Settings,
    isLoading: Boolean,
    workspace: WorkspaceModel,
    allNotes: List<NotesModel>,
    onWorkspaceEvents: (WorkspaceEvents) -> Unit,
    onNotesEvents: (NotesEvents) -> Unit,
    onSettingEvents: (SettingEvents)->Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var addSpaceDialog by remember { mutableStateOf(false) }

    val tabs = remember(workspace, allNotes, settings) {
        buildList {
            add(WorkspaceTab("Home", Icons.Default.Home) { /* Will inject later */ })
            if (workspace.isNotesAdded) add(WorkspaceTab("Notes", Icons.AutoMirrored.Filled.Notes) { NotesHome(navController, workspace.workspaceId, allLabels, settings, isLoading, allNotes, onNotesEvents, onSettingEvents) })
            if (workspace.isTaskAdded) add(WorkspaceTab("Tasks", Icons.Default.TaskAlt) { TaskHome(navController) })
            if (workspace.isCalenderAdded) add(WorkspaceTab("Calendar", Icons.Default.CalendarMonth) { /* CalendarScreen */ })
            if (workspace.isAnalyticsAdded) add(WorkspaceTab("Analytics", Icons.Default.Analytics) { /* AnalyticsScreen */ })
        }
    }.toMutableList()

    tabs[0] = WorkspaceTab("Home", Icons.Default.Home) {
        WorkspaceHomeScreen(
            workspace = workspace,
            onWorkspaceEvents = onWorkspaceEvents,
            onAddSpaces = { addSpaceDialog = true },
            navigateToTab = { title ->
                val index = tabs.indexOfFirst { it.title == title }
                if (index != -1) selectedTabIndex = index
            }
        )
    }

    if(addSpaceDialog){
        AddSpacesDialog(
            workspace=workspace,
            onConfirmation = { onWorkspaceEvents(WorkspaceEvents.UpsertSpace(it)) },
            onDismissRequest = { addSpaceDialog=false }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                title = { Text(workspace.title) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Default.ArrowBack, null) } },
                actions = { WorkspaceMore(showEditLabel = workspace.isNotesAdded, onDelete = {}, onEditDetails = {}, onEditLabel = {
                    navController.navigate(NavRoutes.EditLabels.withArgs(workspace.workspaceId))}) }
            )
        },
        floatingActionButton = {
            when (tabs[selectedTabIndex].title) {
                "Notes" -> {
                    FloatingActionButton(onClick = {
                        navController.navigate(NavRoutes.NoteDetails.withArgs(workspace.workspaceId, -1))
                    }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
                "Tasks" -> {
                    FloatingActionButton(onClick = { navController.navigate(NavRoutes.TaskDetails.route) }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
                else -> {
                    // No FAB shown for "Home", "Calendar", or "Analytics"
                }
            }
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            PrimaryScrollableTabRow(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                edgePadding = 0.dp,
                divider = { },
                selectedTabIndex = selectedTabIndex
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        text = { Text(tab.title) }
                    )
                }
            }

            // Render the selected tab's screen
            tabs[selectedTabIndex].content()
        }
    }
}

@Composable
fun WorkspaceHomeScreen(
    workspace: WorkspaceModel,
    onWorkspaceEvents: (WorkspaceEvents) -> Unit,
    onAddSpaces: () -> Unit,
    navigateToTab: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        onClick = {},
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Workspaces, null, modifier = Modifier.size(40.dp))
                Text(workspace.title, style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(Modifier.height(8.dp))
            Text(workspace.description, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Light))

            LazyColumn(
                Modifier.fillMaxWidth().padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (workspace.isNotesAdded) {
                    item {
                        SpacesCard(
                            title = "Notes",
                            icon = Icons.AutoMirrored.Default.Notes,
                            onClick = { navigateToTab("Notes") },
                            onRemove = { onWorkspaceEvents(WorkspaceEvents.UpsertSpace(workspace.copy(isNotesAdded = false))) }
                        )
                    }
                }
                if (workspace.isTaskAdded) {
                    item {
                        SpacesCard(
                            title = "Tasks",
                            icon = Icons.Default.TaskAlt,
                            onClick = { navigateToTab("Tasks") },
                            onRemove = { onWorkspaceEvents(WorkspaceEvents.UpsertSpace(workspace.copy(isTaskAdded = false))) }
                        )
                    }
                }
                if (workspace.isCalenderAdded) {
                    item {
                        SpacesCard(
                            title = "Calendar",
                            icon = Icons.Default.CalendarMonth,
                            onClick = { navigateToTab("Calendar") },
                            onRemove = { onWorkspaceEvents(WorkspaceEvents.UpsertSpace(workspace.copy(isCalenderAdded = false))) }
                        )
                    }
                }
                if (workspace.isAnalyticsAdded) {
                    item {
                        SpacesCard(
                            title = "Analytics",
                            icon = Icons.Default.Analytics,
                            onClick = { navigateToTab("Analytics") },
                            onRemove = { onWorkspaceEvents(WorkspaceEvents.UpsertSpace(workspace.copy(isAnalyticsAdded = false))) }
                        )
                    }
                }
                if(!workspace.isNotesAdded || !workspace.isTaskAdded || !workspace.isCalenderAdded || !workspace.isAnalyticsAdded){
                    item {
                        SpacesCard(
                            title = "Add Spaces",
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
}

@Composable
fun SpacesCard(
    title: String,
    icon: ImageVector,
    isAddNewSpace: Boolean=false,
    onRemove: ()->Unit,
    onClick: ()->Unit
){
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
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
