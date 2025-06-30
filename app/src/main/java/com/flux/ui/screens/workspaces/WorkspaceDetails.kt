package com.flux.ui.screens.workspaces

import android.app.Activity
import java.io.File
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import com.flux.data.model.HabitInstanceModel
import com.flux.data.model.HabitModel
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import com.flux.data.model.TodoModel
import com.flux.data.model.WorkspaceModel
import com.flux.navigation.NavRoutes
import com.flux.other.canScheduleHabitReminder
import com.flux.other.cancelReminder
import com.flux.other.isNotificationPermissionGranted
import com.flux.other.requestExactAlarmPermission
import com.flux.other.requestNotificationPermission
import com.flux.other.scheduleReminder
import com.flux.ui.components.AddSpacesDialog
import com.flux.ui.components.DeleteAlert
import com.flux.ui.components.HabitBottomSheet
import com.flux.ui.components.NewWorkspaceBottomSheet
import com.flux.ui.components.SetPasskeyDialog
import com.flux.ui.components.WorkspaceMore
import com.flux.ui.events.HabitEvents
import com.flux.ui.events.NotesEvents
import com.flux.ui.events.SettingEvents
import com.flux.ui.events.TaskEvents
import com.flux.ui.events.TodoEvents
import com.flux.ui.events.WorkspaceEvents
import com.flux.ui.screens.calender.Calender
import com.flux.ui.screens.events.EventHome
import com.flux.ui.screens.habits.HabitsHome
import com.flux.ui.screens.notes.NotesHome
import com.flux.ui.screens.todo.TodoHome
import com.flux.ui.state.Settings
import kotlinx.coroutines.launch
import java.io.FileOutputStream

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
    isNotesLoading: Boolean,
    isTodayTaskLoading: Boolean,
    isDatedTaskLoading: Boolean,
    isTodoLoading: Boolean,
    workspace: WorkspaceModel,
    allEvents: List<EventModel>,
    allNotes: List<NotesModel>,
    todayEvents: List<EventModel>,
    datedEvents: List<EventModel>,
    allHabits: List<HabitModel>,
    allLists: List<TodoModel>,
    allHabitInstances: List<HabitInstanceModel>,
    allEventInstances: List<EventInstanceModel>,
    onWorkspaceEvents: (WorkspaceEvents) -> Unit,
    onNotesEvents: (NotesEvents) -> Unit,
    onTaskEvents: (TaskEvents) -> Unit,
    onHabitEvents: (HabitEvents) -> Unit,
    onTodoEvents: (TodoEvents)->Unit,
    onSettingEvents: (SettingEvents) -> Unit,
) {
    val workspaceId=workspace.workspaceId
    val context= LocalContext.current
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var editWorkspaceDialog by remember { mutableStateOf(false) }
    var addSpaceDialog by remember { mutableStateOf(false) }
    var showDeleteWorkspaceDialog by remember { mutableStateOf(false) }
    var showHabitDialog by remember { mutableStateOf(false) }
    var showLockDialog by remember { mutableStateOf(false) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let { onWorkspaceEvents(WorkspaceEvents.UpsertSpace(workspace.copy(cover = copyToInternalStorage(context, uri).toString()))) } }
    )
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if(showDeleteWorkspaceDialog){
        DeleteAlert(onConfirmation = {
            showDeleteWorkspaceDialog=false
            onWorkspaceEvents(WorkspaceEvents.DeleteSpace(workspace))
            navController.popBackStack()
            onNotesEvents(NotesEvents.DeleteAllWorkspaceNotes(workspaceId))
            onTodoEvents(TodoEvents.DeleteAllWorkspaceLists(workspaceId))
            allEvents.forEach { event-> cancelReminder(context, event.eventId, "EVENT") }
            onTaskEvents(TaskEvents.DeleteAllWorkspaceEvents(workspaceId))
            allHabits.forEach { habit-> cancelReminder(context, habit.habitId, "HABIT") }
            onHabitEvents(HabitEvents.DeleteAllWorkspaceHabits(workspaceId))
        }, onDismissRequest = {
            showDeleteWorkspaceDialog=false
        })
    }

    val tabs = remember(workspace, allNotes, allLists, allHabits, allHabitInstances, todayEvents, datedEvents, allEventInstances, settings) {
        buildList {
            add(WorkspaceTab("Home", Icons.Default.Home) { })
            if (workspace.isNotesAdded) add(WorkspaceTab("Notes", Icons.AutoMirrored.Filled.Notes) {
                NotesHome(navController, workspaceId, allLabels, settings, isNotesLoading, allNotes, onNotesEvents, onSettingEvents)
            })
            if (workspace.isTodoAdded) add(WorkspaceTab("To-do", Icons.Default.Checklist) {
                TodoHome(navController, settings.data.cornerRadius, allLists, workspaceId, isTodoLoading, onTodoEvents)
            })
            if (workspace.isEventsAdded) add(WorkspaceTab("Events", Icons.Default.Event) {
                EventHome(navController, settings.data.cornerRadius, isTodayTaskLoading, todayEvents, allEventInstances, workspaceId, onTaskEvents)
            })
            if (workspace.isCalenderAdded) add(WorkspaceTab("Calendar", Icons.Default.CalendarMonth) {
                Calender(navController, settings.data.cornerRadius, isDatedTaskLoading, workspaceId, settings, datedEvents, allEventInstances, onSettingEvents, onTaskEvents)
            })
            if (workspace.isHabitsAdded) add(WorkspaceTab("Habits", Icons.Default.EventAvailable) {
                HabitsHome(navController, settings.data.cornerRadius, workspaceId, allHabits, allHabitInstances, onHabitEvents)
            })
        }
    }.toMutableList()

    tabs[0] = WorkspaceTab("Home", Icons.Default.Home) {
        WorkspaceHomeScreen(
            radius = settings.data.cornerRadius,
            workspace = workspace,
            allEvents = allEvents,
            allHabits = allHabits,
            onWorkspaceEvents = onWorkspaceEvents,
            onAddSpaces = { addSpaceDialog = true },
            navigateToTab = { title ->
                val index = tabs.indexOfFirst { it.title == title }
                if (index != -1) selectedTabIndex = index
            },
            onTaskEvents = onTaskEvents,
            onTodoEvents = onTodoEvents,
            onNotesEvents = onNotesEvents,
            onHabitEvents = onHabitEvents
        )
    }

    if (addSpaceDialog) {
        AddSpacesDialog(
            workspace = workspace,
            onConfirmation = { onWorkspaceEvents(WorkspaceEvents.UpsertSpace(it)) },
            onDismissRequest = { addSpaceDialog = false }
        )
    }

    if(showLockDialog){ SetPasskeyDialog({onWorkspaceEvents(WorkspaceEvents.UpsertSpace(workspace.copy(passKey = it)))}) { showLockDialog=false } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                title = {
                    Text(
                        workspace.title,
                        maxLines = 1,
                        modifier = Modifier.widthIn(max = 300.dp),
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Default.ArrowBack, null) } },
                actions = {
                    WorkspaceMore(
                        isLocked = workspace.passKey.isNotBlank(),
                        isPinned = workspace.isPinned,
                        showEditLabel = workspace.isNotesAdded,
                        onDelete = { showDeleteWorkspaceDialog=true },
                        onEditDetails = { editWorkspaceDialog = true },
                        onEditLabel = { navController.navigate(NavRoutes.EditLabels.withArgs(workspaceId)) },
                        onAddCover = { imagePickerLauncher.launch("image/*") },
                        onTogglePinned = { onWorkspaceEvents(WorkspaceEvents.UpsertSpace(workspace.copy(isPinned = !workspace.isPinned))) },
                        onToggleLock = { if(workspace.passKey.isNotBlank()) onWorkspaceEvents(WorkspaceEvents.UpsertSpace(workspace.copy(passKey = ""))) else showLockDialog=true }
                    )
                }
            )
        },
        floatingActionButton = {
            when (tabs[selectedTabIndex].title) {
                "Notes" -> {
                    FloatingActionButton(onClick = {
                        navController.navigate(NavRoutes.NoteDetails.withArgs(workspaceId, -1))
                    }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
                "Events" -> {
                    FloatingActionButton(onClick = {
                        navController.navigate(NavRoutes.EventDetails.withArgs(workspaceId, -1))
                    }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
                "Habits" -> {
                    FloatingActionButton(onClick = {
                        if(!canScheduleHabitReminder(context)) requestExactAlarmPermission(context)
                        if(!isNotificationPermissionGranted(context)) requestNotificationPermission(context as Activity)
                        if(canScheduleHabitReminder(context) && isNotificationPermissionGranted(context)){
                            showHabitDialog = true
                            scope.launch { sheetState.show() }
                        }
                    }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
                "To-do" -> {
                    FloatingActionButton(onClick = {
                        navController.navigate(NavRoutes.TodoDetail.withArgs(workspaceId, -1))
                    }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            PrimaryScrollableTabRow(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                edgePadding = 0.dp,
                divider = {},
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

            tabs[selectedTabIndex].content()
        }

        // Edit Workspace Sheet
        NewWorkspaceBottomSheet(
            isEditing = true,
            workspace = workspace,
            isVisible = editWorkspaceDialog,
            sheetState = sheetState,
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    editWorkspaceDialog = false
                }
            },
            onConfirm = {
                onWorkspaceEvents(WorkspaceEvents.UpsertSpace(it))
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    editWorkspaceDialog = false
                }
            }
        )

        // Habit Bottom Sheet — always composed
        HabitBottomSheet(
            isVisible = showHabitDialog,
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showHabitDialog = false
                }
            },
            onConfirm = { newHabit->
                scheduleReminder(
                    context = context,
                    id = newHabit.habitId,
                    type="HABIT",
                    repeat = "DAILY",
                    timeInMillis = newHabit.startDateTime,
                    title = newHabit.title,
                    description = newHabit.description
                )
                onHabitEvents(HabitEvents.UpsertHabit(newHabit.copy(workspaceId = workspaceId)))
                scope.launch { sheetState.hide() }.invokeOnCompletion { showHabitDialog = false }
            }
        )
    }
}

fun copyToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "img_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
