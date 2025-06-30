package com.flux.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import com.flux.data.model.HabitModel
import com.flux.data.model.NotesModel
import com.flux.data.model.TodoModel
import com.flux.ui.screens.auth.AuthScreen
import com.flux.ui.screens.events.EventDetails
import com.flux.ui.screens.habits.HabitDetails
import com.flux.ui.screens.notes.EditLabels
import com.flux.ui.screens.notes.NoteDetails
import com.flux.ui.screens.settings.About
import com.flux.ui.screens.settings.Contact
import com.flux.ui.screens.settings.Customize
import com.flux.ui.screens.settings.Languages
import com.flux.ui.screens.settings.Privacy
import com.flux.ui.screens.settings.Settings
import com.flux.ui.screens.todo.TodoDetail
import com.flux.ui.screens.workspaces.WorkSpaces
import com.flux.ui.screens.workspaces.WorkspaceDetails
import com.flux.ui.state.States
import com.flux.ui.viewModel.ViewModels
import java.time.LocalDate
import kotlin.String
import kotlin.Unit

sealed class NavRoutes(val route: String) {
    // auth screen
    data object AuthScreen: NavRoutes("biometric")

    // workspaces
    data object Workspace : NavRoutes ("workspace")
    data object WorkspaceHome : NavRoutes ("workspace/details")

    //Labels
    data object EditLabels : NavRoutes ("workspace/labels/edit")

    // Notes
    data object NoteDetails : NavRoutes("workspace/note/details")

    // Habits
    data object HabitDetails : NavRoutes("workspace/habit/details")

    // Events
    data object EventDetails : NavRoutes("workspace/event/details")

    // TodoList
    data object TodoDetail : NavRoutes("workspace/todo/details")

    // Settings
    data object Settings : NavRoutes("settings")
    data object Privacy : NavRoutes("settings/privacy")
    data object Customize : NavRoutes("settings/customize")
    data object Languages : NavRoutes("settings/language")
    data object About : NavRoutes("settings/about")
    data object Contact : NavRoutes("settings/contact")

    fun withArgs(vararg args: Int): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

val AuthScreen =
    mapOf<String, @Composable (navController: NavController, states: States) -> Unit>(
        NavRoutes.AuthScreen.route to { navController, states ->
            AuthScreen(navController, states.settings.data.isBiometricEnabled)
        }
    )

val NotesScreens =
    mapOf<String, @Composable (navController: NavController, snackbarHostState: SnackbarHostState, notesId: Int, workspaceId: Int, states: States, viewModels: ViewModels) -> Unit>(
        NavRoutes.NoteDetails.route + "/{workspaceId}" + "/{notesId}" to { navController, snackbarHostState, notesId, workspaceId, states, viewModel ->
            NoteDetails(navController, workspaceId, states.notesState.allNotes.find { it.notesId==notesId }?: NotesModel(workspaceId=workspaceId), states.notesState.allLabels.filter { it.workspaceId==workspaceId }, snackbarHostState, viewModel.notesViewModel::onEvent)
        }
    )

val HabitScreens =
    mapOf<String, @Composable (navController: NavController, habitId: Int, workspaceId: Int, states: States, viewModels: ViewModels) -> Unit>(
        NavRoutes.HabitDetails.route + "/{workspaceId}" + "/{habitId}" to { navController, habitId, workspaceId, states, viewModel ->
            HabitDetails(navController, states.settings.data.cornerRadius, workspaceId, states.habitState.allHabits.find { it.habitId==habitId }?: HabitModel(workspaceId=workspaceId), states.habitState.allInstances.filter { it.habitId==habitId }, viewModel.habitViewModel::onEvent)
        }
    )

val TodoScreens =
    mapOf<String, @Composable (navController: NavController, listId: Int, workspaceId: Int, states: States, viewModels: ViewModels) -> Unit>(
        NavRoutes.TodoDetail.route + "/{workspaceId}" + "/{listId}" to { navController, listId, workspaceId, states, viewModel ->
            TodoDetail(navController, states.todoState.allLists.find { it.id==listId }?: TodoModel(workspaceId=workspaceId), workspaceId, viewModel.todoViewModel::onEvent)
        }
    )

val SettingsScreens =
    mapOf<String, @Composable (navController: NavController, snackbarHostState: SnackbarHostState, states: States, viewModels: ViewModels) -> Unit> (
        NavRoutes.Settings.route to { navController, _, states, viewModels ->
            Settings(navController, states.settings)
        },
        NavRoutes.Privacy.route to { navController, _, states, viewModels ->
            Privacy(navController, states.settings, viewModels.settingsViewModel::onEvent)
        },
        NavRoutes.About.route to { navController, _, states, _ ->
            About(navController, states.settings.data.cornerRadius)
        },
        NavRoutes.Languages.route to { navController, _, states, _ ->
            Languages(navController, states.settings)
        },
        NavRoutes.Customize.route to { navController, _, states, viewModels ->
            Customize(navController, states.settings, viewModels.settingsViewModel::onEvent)
        },
        NavRoutes.Contact.route to { navController, _, states, _ ->
            Contact(navController, states.settings.data.cornerRadius)
        }
    )

val EventScreens =
    mapOf<String, @Composable (navController: NavController, states: States, viewModels: ViewModels, eventId: Int, workspaceId: Int) -> Unit> (
        NavRoutes.EventDetails.route + "/{workspaceId}" + "/{eventId}" to { navController, states, viewModels, eventId, workspaceId ->
            EventDetails(navController, states.eventState.allEvent.find { it.eventId==eventId }?: EventModel(workspaceId = workspaceId), states.eventState.allEventInstances.find { it.eventId==eventId && it.instanceDate==LocalDate.now() }?: EventInstanceModel(eventId=eventId, instanceDate = LocalDate.now()), viewModels.eventViewModel::onEvent)
        }
    )

val WorkspaceScreens =
    mapOf<String, @Composable (navController: NavController, snackbarHostState: SnackbarHostState, states: States, viewModels: ViewModels, workspaceId: Int) -> Unit> (
        NavRoutes.Workspace.route to { navController, snackbarHostState, states, viewModels, _ ->
            WorkSpaces(snackbarHostState, navController, states.settings.data.cornerRadius, states.workspaceState.allSpaces, viewModels.notesViewModel::onEvent, viewModels.eventViewModel::onEvent, viewModels.habitViewModel::onEvent, viewModels.todoViewModel::onEvent, viewModels.workspaceViewModel::onEvent)
        },
        NavRoutes.WorkspaceHome.route + "/{workspaceId}" to { navController, snackbarHostState, states, viewModels, workspaceId ->
            WorkspaceDetails(
                navController,
                states.notesState.allLabels.filter { it.workspaceId==workspaceId },
                states.settings, states.notesState.isLoading, states.eventState.isTodayEventLoading,
                states.eventState.isDatedEventLoading,
                states.todoState.isLoading,
                states.workspaceState.allSpaces.first { it.workspaceId==workspaceId },
                states.eventState.allEvent,
                states.notesState.allNotes.filter { it.workspaceId==workspaceId },
                states.eventState.todayEvents,
                states.eventState.datedEvents,
                states.habitState.allHabits,
                states.todoState.allLists,
                states.habitState.allInstances,
                states.eventState.allEventInstances,
                viewModels.workspaceViewModel::onEvent,
                viewModels.notesViewModel::onEvent,
                viewModels.eventViewModel::onEvent,
                viewModels.habitViewModel::onEvent,
                viewModels.todoViewModel::onEvent,
                viewModels.settingsViewModel::onEvent)
        }
    )

val LabelScreens =
    mapOf<String, @Composable (navController: NavController, states: States, viewModels: ViewModels, workspaceId: Int) -> Unit> (
        NavRoutes.EditLabels.route + "/{workspaceId}" to { navController, states, viewModels, workspaceId ->
            EditLabels(navController, workspaceId, states.notesState.allLabels.filter { it.workspaceId==workspaceId }, viewModels.notesViewModel::onEvent)
        }
    )
