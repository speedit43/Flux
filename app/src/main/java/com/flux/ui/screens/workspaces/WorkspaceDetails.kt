package com.flux.ui.screens.workspaces

import java.io.File
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.R
import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import com.flux.data.model.HabitInstanceModel
import com.flux.data.model.HabitModel
import com.flux.data.model.JournalModel
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel
import com.flux.data.model.TodoModel
import com.flux.data.model.WorkspaceModel
import com.flux.navigation.NavRoutes
import com.flux.other.canScheduleReminder
import com.flux.other.cancelReminder
import com.flux.other.isNotificationPermissionGranted
import com.flux.other.openAppNotificationSettings
import com.flux.other.requestExactAlarmPermission
import com.flux.ui.components.AddSpacesDialog
import com.flux.ui.components.DeleteAlert
import com.flux.ui.components.HabitBottomSheet
import com.flux.ui.components.NewWorkspaceBottomSheet
import com.flux.ui.components.SetPasskeyDialog
import com.flux.ui.components.WorkspaceMore
import com.flux.ui.events.HabitEvents
import com.flux.ui.events.JournalEvents
import com.flux.ui.events.NotesEvents
import com.flux.ui.events.SettingEvents
import com.flux.ui.events.TaskEvents
import com.flux.ui.events.TodoEvents
import com.flux.ui.events.WorkspaceEvents
import com.flux.ui.screens.analytics.Analytics
import com.flux.ui.screens.calender.Calender
import com.flux.ui.screens.events.EventHome
import com.flux.ui.screens.habits.HabitsHome
import com.flux.ui.screens.journal.JournalHome
import com.flux.ui.screens.notes.NotesHome
import com.flux.ui.screens.todo.TodoHome
import com.flux.ui.state.Settings
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class WorkspaceTab(
    val titleId: Int,
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
    isJournalEntriesLoading: Boolean,
    isJournalEntriesLoadingMore: Boolean,
    workspace: WorkspaceModel,
    allEvents: List<EventModel>,
    allNotes: List<NotesModel>,
    todayEvents: List<EventModel>,
    datedEvents: List<EventModel>,
    allHabits: List<HabitModel>,
    allLists: List<TodoModel>,
    allEntries: List<JournalModel>,
    allHabitInstances: List<HabitInstanceModel>,
    allEventInstances: List<EventInstanceModel>,
    onWorkspaceEvents: (WorkspaceEvents) -> Unit,
    onNotesEvents: (NotesEvents) -> Unit,
    onTaskEvents: (TaskEvents) -> Unit,
    onHabitEvents: (HabitEvents) -> Unit,
    onTodoEvents: (TodoEvents)->Unit,
    onJournalEvents: (JournalEvents)->Unit,
    onSettingEvents: (SettingEvents) -> Unit,
) {
    val radius= settings.data.cornerRadius
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
            allEvents.forEach { event-> cancelReminder(context, event.eventId, "EVENT", event.title, event.description, event.repetition.toString()) }
            onTaskEvents(TaskEvents.DeleteAllWorkspaceEvents(workspaceId))
            allHabits.forEach { habit-> cancelReminder(context, habit.habitId, "HABIT", habit.title, habit.description, "DAILY") }
            onHabitEvents(HabitEvents.DeleteAllWorkspaceHabits(workspaceId))
        }, onDismissRequest = {
            showDeleteWorkspaceDialog=false
        })
    }

    val tabs = remember(workspace, isNotesLoading, isJournalEntriesLoading, isJournalEntriesLoadingMore, isTodoLoading, isDatedTaskLoading, isTodayTaskLoading, allEntries, allNotes, allLists, allHabits, allHabitInstances, todayEvents, datedEvents, allEventInstances, settings) {
        buildList {
            add(WorkspaceTab(R.string.Home, Icons.Default.Home) { })
            if (workspace.isNotesAdded) add(WorkspaceTab(R.string.Notes, Icons.AutoMirrored.Filled.Notes) {
                NotesHome(navController, workspaceId, allLabels, settings, isNotesLoading, allNotes, onNotesEvents, onSettingEvents)
            })
            if (workspace.isJournalAdded) add(WorkspaceTab(R.string.Journal, Icons.Default.AutoStories) {
                JournalHome(navController, isJournalEntriesLoadingMore, workspaceId, allEntries, onJournalEvents)
            })
            if (workspace.isTodoAdded) add(WorkspaceTab(R.string.To_Do, Icons.Default.Checklist) {
                TodoHome(navController, radius, allLists, workspaceId, isTodoLoading, onTodoEvents)
            })
            if (workspace.isEventsAdded) add(WorkspaceTab(R.string.Events, Icons.Default.Event) {
                EventHome(navController, radius, isTodayTaskLoading, todayEvents, allEventInstances, workspaceId, onTaskEvents)
            })
            if (workspace.isCalenderAdded) add(WorkspaceTab(R.string.Calender, Icons.Default.CalendarMonth) {
                Calender(navController, radius, isDatedTaskLoading, workspaceId, settings, datedEvents, allEventInstances, onSettingEvents, onTaskEvents)
            })
            if (workspace.isHabitsAdded) add(WorkspaceTab(R.string.Habits, Icons.Default.EventAvailable) {
                HabitsHome(navController, radius, workspaceId, allHabits, allHabitInstances, onHabitEvents)
            })
            if (workspace.isAnalyticsAdded) add(WorkspaceTab(R.string.Analytics, Icons.Default.Analytics) {
                Analytics(workspace, radius, allHabitInstances, allHabits.distinctBy { it.habitId }.count(), allNotes.size, allEntries, allEvents, allEventInstances)
            })
        }
    }.toMutableList()

    tabs[0] = WorkspaceTab(R.string.Home, Icons.Default.Home) {
        WorkspaceHomeScreen(
            radius = radius,
            workspace = workspace,
            allEvents = allEvents,
            allHabits = allHabits,
            onWorkspaceEvents = onWorkspaceEvents,
            onAddSpaces = { addSpaceDialog = true },
            navigateToTab = { title ->
                val index = tabs.indexOfFirst { it.titleId == title }
                if (index != -1) selectedTabIndex = index
            },
            onTaskEvents = onTaskEvents,
            onTodoEvents = onTodoEvents,
            onNotesEvents = onNotesEvents,
            onHabitEvents = onHabitEvents,
            onJournalEvents = onJournalEvents
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
            when (tabs[selectedTabIndex].titleId) {
                R.string.Notes -> {
                    FloatingActionButton(onClick = {
                        navController.navigate(NavRoutes.NoteDetails.withArgs(workspaceId, -1))
                    }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
                R.string.Events -> {
                    FloatingActionButton(onClick = {
                        if(!canScheduleReminder(context)) {
                            Toast.makeText(context, context.getText(R.string.Reminder_Permission), Toast.LENGTH_SHORT).show()
                            requestExactAlarmPermission(context)
                        }
                        if(!isNotificationPermissionGranted(context)) {
                            Toast.makeText(context, context.getText(R.string.Notification_Permission), Toast.LENGTH_SHORT).show()
                            openAppNotificationSettings(context)
                        }
                        if(canScheduleReminder(context) && isNotificationPermissionGranted(context)){
                            navController.navigate(NavRoutes.EventDetails.withArgs(workspaceId, -1))
                        }
                    }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
                R.string.Habits -> {
                    FloatingActionButton(onClick = {
                        if(!canScheduleReminder(context)){
                            Toast.makeText(context, context.getText(R.string.Reminder_Permission), Toast.LENGTH_SHORT).show()
                            requestExactAlarmPermission(context)
                        }
                        if(!isNotificationPermissionGranted(context)) {
                            Toast.makeText(context, context.getText(R.string.Notification_Permission), Toast.LENGTH_SHORT).show()
                            openAppNotificationSettings(context)
                        }
                        if(canScheduleReminder(context) && isNotificationPermissionGranted(context)){
                            showHabitDialog = true
                            scope.launch { sheetState.show() }
                        }
                    }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
                R.string.To_Do -> {
                    FloatingActionButton(onClick = {
                        navController.navigate(NavRoutes.TodoDetail.withArgs(workspaceId, -1))
                    }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
                R.string.Journal -> {
                    if(allEntries.none{
                        LocalDate.now() == Instant.ofEpochMilli(it.dateTime).atZone(ZoneId.systemDefault()).toLocalDate() }){
                        ExtendedFloatingActionButton(
                            onClick = {navController.navigate(NavRoutes.EditJournal.withArgs(workspaceId, -1)) },
                            icon = { Icon(Icons.Filled.Add, "Extended floating action button.") },
                            text = { Text(stringResource(R.string.Today_Entry)) },
                        )
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
                        icon = { Icon(tab.icon, contentDescription = stringResource(tab.titleId)) },
                        text = { Text(stringResource(tab.titleId)) }
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
            onDismiss = { scope.launch { sheetState.hide() }.invokeOnCompletion { editWorkspaceDialog = false } },
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
            onDismissRequest = { scope.launch { sheetState.hide() }.invokeOnCompletion { showHabitDialog = false } },
            onConfirm = { newHabit, adjustedTime->
                onHabitEvents(HabitEvents.UpsertHabit(context, newHabit.copy(workspaceId = workspaceId), adjustedTime))
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
